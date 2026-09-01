package com.forcreators.api.web;

import com.forcreators.api.domain.OrderStatus;
import com.forcreators.api.domain.RequestStatus;
import com.forcreators.api.dto.ApiDtos.*;
import com.forcreators.api.service.CatalogService;
import com.forcreators.api.service.ContentService;
import com.forcreators.api.service.OrderService;
import com.forcreators.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService products;
    private final CatalogService catalog;
    private final OrderService orders;
    private final ContentService content;

    public AdminController(
            ProductService products,
            CatalogService catalog,
            OrderService orders,
            ContentService content
    ) {
        this.products = products;
        this.catalog = catalog;
        this.orders = orders;
        this.content = content;
    }

    @PostMapping("/products")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return products.create(request);
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return products.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    public MessageResponse deleteProduct(@PathVariable Long id) {
        products.delete(id);
        return new MessageResponse("Deleted");
    }

    @PostMapping("/categories")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return catalog.createCategory(request);
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return catalog.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public MessageResponse deleteCategory(@PathVariable Long id) {
        catalog.deleteCategory(id);
        return new MessageResponse("Deleted");
    }

    @PostMapping("/artisans")
    public ArtisanResponse createArtisan(@Valid @RequestBody ArtisanRequest request) {
        return catalog.createArtisan(request);
    }

    @PutMapping("/artisans/{id}")
    public ArtisanResponse updateArtisan(@PathVariable Long id, @Valid @RequestBody ArtisanRequest request) {
        return catalog.updateArtisan(id, request);
    }

    @DeleteMapping("/artisans/{id}")
    public MessageResponse deleteArtisan(@PathVariable Long id) {
        catalog.deleteArtisan(id);
        return new MessageResponse("Deleted");
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders() {
        return orders.all();
    }

    @PatchMapping("/orders/{id}")
    public OrderResponse updateOrder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orders.updateStatus(id, OrderStatus.valueOf(body.get("status")));
    }

    @GetMapping("/custom-orders")
    public List<CustomOrderResponse> customOrders() {
        return content.listCustom();
    }

    @PatchMapping("/custom-orders/{id}")
    public CustomOrderResponse updateCustom(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return content.updateCustomStatus(id, RequestStatus.valueOf(body.get("status")));
    }

    @PostMapping("/journal")
    public JournalResponse createJournal(@Valid @RequestBody JournalRequest request) {
        return content.createJournal(request);
    }

    @DeleteMapping("/journal/{id}")
    public MessageResponse deleteJournal(@PathVariable Long id) {
        content.deleteJournal(id);
        return new MessageResponse("Deleted");
    }
}
