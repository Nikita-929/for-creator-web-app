package com.forcreators.api.service;

import com.forcreators.api.domain.Product;
import com.forcreators.api.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    public static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("5000");
    public static final BigDecimal FLAT_SHIPPING = new BigDecimal("99.00");

    public BigDecimal lineTotal(BigDecimal unitPrice, int quantity) {
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new BusinessException("Price must be non-negative");
        }
        if (quantity < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal shippingFee(BigDecimal subtotal) {
        if (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return FLAT_SHIPPING;
    }

    public BigDecimal orderTotal(BigDecimal subtotal) {
        return subtotal.add(shippingFee(subtotal)).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isSoldOut(Product product) {
        return product.getStockQuantity() == null || product.getStockQuantity() <= 0;
    }
}
