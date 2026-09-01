package com.forcreators.api.service;

import com.forcreators.api.domain.Product;
import com.forcreators.api.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private final InventoryService inventory = new InventoryService();

    @Test
    void decrementReducesStock() {
        Product product = Product.builder().name("Tray").stockQuantity(5).oneOfAKind(false).build();
        inventory.decrement(product, 2);
        assertEquals(3, product.getStockQuantity());
    }

    @Test
    void uniquePieceCannotBeBoughtInQuantity() {
        Product product = Product.builder().name("Moon Jar").stockQuantity(1).oneOfAKind(true).build();
        assertThrows(BusinessException.class, () -> inventory.assertAvailable(product, 2));
    }

    @Test
    void soldOutThrows() {
        Product product = Product.builder().name("Scarf").stockQuantity(0).oneOfAKind(false).build();
        assertThrows(BusinessException.class, () -> inventory.assertAvailable(product, 1));
    }
}
