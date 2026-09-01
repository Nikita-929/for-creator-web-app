package com.forcreators.api.service;

import com.forcreators.api.domain.Product;
import com.forcreators.api.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private final PricingService pricing = new PricingService();

    @Test
    void lineTotalMultipliesAndScales() {
        assertEquals(new BigDecimal("5400.00"), pricing.lineTotal(new BigDecimal("2700"), 2));
    }

    @Test
    void shippingIsFlatUnderThreshold() {
        assertEquals(new BigDecimal("99.00"), pricing.shippingFee(new BigDecimal("4999.99")));
    }

    @Test
    void shippingIsFreeAtThreshold() {
        assertEquals(new BigDecimal("0.00"), pricing.shippingFee(new BigDecimal("5000")));
        assertEquals(new BigDecimal("5000.00"), pricing.orderTotal(new BigDecimal("5000")));
    }

    @Test
    void rejectsInvalidQuantity() {
        assertThrows(BusinessException.class, () -> pricing.lineTotal(BigDecimal.TEN, 0));
    }

    @Test
    void soldOutWhenStockZero() {
        Product product = Product.builder().stockQuantity(0).build();
        assertTrue(pricing.isSoldOut(product));
    }
}
