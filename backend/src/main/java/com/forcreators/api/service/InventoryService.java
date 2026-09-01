package com.forcreators.api.service;

import com.forcreators.api.domain.Product;
import com.forcreators.api.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public void assertAvailable(Product product, int quantity) {
        if (product.isOneOfAKind() && quantity > 1) {
            throw new BusinessException("This one-of-a-kind piece can only be purchased once");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
            throw new BusinessException(product.getName() + " is sold out or has insufficient stock");
        }
    }

    public void decrement(Product product, int quantity) {
        assertAvailable(product, quantity);
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }
}
