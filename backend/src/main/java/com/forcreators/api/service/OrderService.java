package com.forcreators.api.service;

import com.forcreators.api.domain.*;
import com.forcreators.api.dto.ApiDtos.CartItemRequest;
import com.forcreators.api.dto.ApiDtos.CheckoutRequest;
import com.forcreators.api.dto.ApiDtos.OrderResponse;
import com.forcreators.api.dto.ApiDtos.PaymentVerifyRequest;
import com.forcreators.api.exception.BusinessException;
import com.forcreators.api.exception.NotFoundException;
import com.forcreators.api.repository.ProductRepository;
import com.forcreators.api.repository.ShopOrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

@Service
public class OrderService {

    private final ShopOrderRepository orders;
    private final ProductRepository products;
    private final PricingService pricingService;
    private final InventoryService inventoryService;
    private final DtoMapper mapper;
    private final String razorpayKeyId;
    private final String razorpayKeySecret;

    public OrderService(
            ShopOrderRepository orders,
            ProductRepository products,
            PricingService pricingService,
            InventoryService inventoryService,
            DtoMapper mapper,
            @Value("${app.razorpay.key-id:}") String razorpayKeyId,
            @Value("${app.razorpay.key-secret:}") String razorpayKeySecret
    ) {
        this.orders = orders;
        this.products = products;
        this.pricingService = pricingService;
        this.inventoryService = inventoryService;
        this.mapper = mapper;
        this.razorpayKeyId = razorpayKeyId;
        this.razorpayKeySecret = razorpayKeySecret;
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request, UserAccount user) {
        if (user == null && (request.guestEmail() == null || request.guestEmail().isBlank())) {
            throw new BusinessException("Guest checkout requires an email");
        }
        ShopOrder order = ShopOrder.builder()
                .user(user)
                .guestEmail(user == null ? request.guestEmail() : user.getEmail())
                .guestName(user == null ? request.guestName() : user.getName())
                .shippingAddress(AuthService.sanitize(request.shippingAddress()))
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItemRequest item : request.items()) {
            Product product = products.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + item.productId()));
            inventoryService.assertAvailable(product, item.quantity());
            BigDecimal line = pricingService.lineTotal(product.getPrice(), item.quantity());
            subtotal = subtotal.add(line);
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(item.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.getItems().add(orderItem);
        }
        order.setSubtotal(subtotal);
        order.setShippingFee(pricingService.shippingFee(subtotal));
        order.setTotal(pricingService.orderTotal(subtotal));
        order.setRazorpayOrderId(createRazorpayOrder(order.getTotal()));
        return mapper.toOrder(orders.save(order), publicKey());
    }

    @Transactional
    public OrderResponse verifyPayment(PaymentVerifyRequest request) {
        ShopOrder order = orders.findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!verifySignature(request)) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orders.save(order);
            throw new BusinessException("Payment signature could not be verified");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return mapper.toOrder(order, publicKey());
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            inventoryService.decrement(product, item.getQuantity());
            products.save(product);
        }
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        order.setRazorpayPaymentId(request.razorpayPaymentId());
        order.setRazorpaySignature(request.razorpaySignature());
        return mapper.toOrder(orders.save(order), publicKey());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> forUser(Long userId) {
        return orders.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(o -> mapper.toOrder(o, publicKey()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> all() {
        return orders.findAll().stream().map(o -> mapper.toOrder(o, publicKey())).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        ShopOrder order = orders.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(status);
        return mapper.toOrder(orders.save(order), publicKey());
    }

    private String publicKey() {
        return StringUtils.hasText(razorpayKeyId) ? razorpayKeyId : "rzp_test_demo";
    }

    private String createRazorpayOrder(BigDecimal total) {
        if (!StringUtils.hasText(razorpayKeyId) || !StringUtils.hasText(razorpayKeySecret)) {
            return "order_demo_" + System.currentTimeMillis();
        }
        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject options = new JSONObject();
            options.put("amount", total.multiply(BigDecimal.valueOf(100)).longValue());
            options.put("currency", "INR");
            options.put("receipt", "fc_" + System.currentTimeMillis());
            return client.orders.create(options).get("id");
        } catch (RazorpayException e) {
            throw new BusinessException("Unable to start Razorpay order: " + e.getMessage());
        }
    }

    private boolean verifySignature(PaymentVerifyRequest request) {
        if (!StringUtils.hasText(razorpayKeySecret)) {
            return request.razorpayOrderId() != null && request.razorpayOrderId().startsWith("order_demo_");
        }
        try {
            String payload = request.razorpayOrderId() + "|" + request.razorpayPaymentId();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return expected.equals(request.razorpaySignature());
        } catch (Exception e) {
            return false;
        }
    }
}
