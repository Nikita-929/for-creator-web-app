package com.forcreators.api.web;

import com.forcreators.api.dto.ApiDtos.*;
import com.forcreators.api.security.CurrentUser;
import com.forcreators.api.service.ContentService;
import com.forcreators.api.service.OrderService;
import com.forcreators.api.service.ReviewService;
import com.forcreators.api.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StorefrontController {

    private final OrderService orders;
    private final ReviewService reviews;
    private final WishlistService wishlist;
    private final ContentService content;
    private final CurrentUser currentUser;

    public StorefrontController(
            OrderService orders,
            ReviewService reviews,
            WishlistService wishlist,
            ContentService content,
            CurrentUser currentUser
    ) {
        this.orders = orders;
        this.reviews = reviews;
        this.wishlist = wishlist;
        this.content = content;
        this.currentUser = currentUser;
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return orders.checkout(request, currentUser.optional());
    }

    @PostMapping("/payments/verify")
    public OrderResponse verify(@Valid @RequestBody PaymentVerifyRequest request) {
        return orders.verifyPayment(request);
    }

    @GetMapping("/orders")
    public List<OrderResponse> myOrders() {
        return orders.forUser(currentUser.require().getId());
    }

    @GetMapping("/reviews/product/{productId}")
    public List<ReviewResponse> reviews(@PathVariable Long productId) {
        return reviews.forProduct(productId);
    }

    @PostMapping("/reviews/product/{productId}")
    public ReviewResponse addReview(@PathVariable Long productId, @Valid @RequestBody ReviewRequest request) {
        return reviews.create(productId, currentUser.require(), request);
    }

    @GetMapping("/wishlist")
    public List<ProductCard> wishlist() {
        return wishlist.list(currentUser.require());
    }

    @PostMapping("/wishlist/{productId}")
    public List<ProductCard> toggleWishlist(@PathVariable Long productId) {
        return wishlist.toggle(currentUser.require(), productId);
    }

    @PostMapping("/contact")
    public MessageResponse contact(@Valid @RequestBody ContactRequest request) {
        return content.contact(request);
    }

    @PostMapping("/custom-orders")
    public CustomOrderResponse custom(@Valid @RequestBody CustomOrderRequestDto request) {
        return content.submitCustom(request);
    }

    @PostMapping("/newsletter")
    public MessageResponse newsletter(@Valid @RequestBody NewsletterRequest request) {
        return content.subscribe(request);
    }

    @GetMapping("/journal")
    public List<JournalResponse> journal() {
        return content.journal();
    }

    @GetMapping("/journal/{slug}")
    public JournalResponse journalPost(@PathVariable String slug) {
        return content.journalBySlug(slug);
    }
}
