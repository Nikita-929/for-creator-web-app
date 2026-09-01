package com.forcreators.api.web;

import com.forcreators.api.domain.CategoryType;
import com.forcreators.api.dto.ApiDtos.ProductCard;
import com.forcreators.api.dto.ApiDtos.ProductResponse;
import com.forcreators.api.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping
    public Page<ProductCard> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String technique,
            @RequestParam(required = false) Long artisanId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return products.search(categoryId, type, material, minPrice, maxPrice, technique, artisanId, q, sort, page, size);
    }

    @GetMapping("/featured")
    public List<ProductCard> featured() {
        return products.featured();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return products.get(id);
    }

    @GetMapping("/{id}/related")
    public List<ProductCard> related(@PathVariable Long id) {
        return products.related(id);
    }
}
