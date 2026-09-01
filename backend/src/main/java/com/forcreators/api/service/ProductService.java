package com.forcreators.api.service;

import com.forcreators.api.domain.Category;
import com.forcreators.api.domain.CategoryType;
import com.forcreators.api.domain.Product;
import com.forcreators.api.dto.ApiDtos.ProductCard;
import com.forcreators.api.dto.ApiDtos.ProductRequest;
import com.forcreators.api.dto.ApiDtos.ProductResponse;
import com.forcreators.api.exception.NotFoundException;
import com.forcreators.api.repository.ArtisanRepository;
import com.forcreators.api.repository.CategoryRepository;
import com.forcreators.api.repository.ProductRepository;
import com.forcreators.api.repository.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final ArtisanRepository artisans;
    private final DtoMapper mapper;

    public ProductService(
            ProductRepository products,
            CategoryRepository categories,
            ArtisanRepository artisans,
            DtoMapper mapper
    ) {
        this.products = products;
        this.categories = categories;
        this.artisans = artisans;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductCard> search(
            Long categoryId,
            CategoryType type,
            String material,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String technique,
            Long artisanId,
            String q,
            String sort,
            int page,
            int size
    ) {
        Sort s = switch (sort == null ? "newest" : sort) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "name" -> Sort.by("name").ascending();
            default -> Sort.by("createdAt").descending();
        };
        return products.findAll(
                ProductSpecifications.withFilters(categoryId, type, material, minPrice, maxPrice, technique, artisanId, q),
                PageRequest.of(page, size, s)
        ).map(mapper::toCard);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return mapper.toProduct(load(id));
    }

    @Transactional(readOnly = true)
    public List<ProductCard> featured() {
        return products.findTop8ByOrderByCreatedAtDesc().stream().map(mapper::toCard).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCard> related(Long id) {
        Product product = load(id);
        return products.findAll().stream()
                .filter(p -> !p.getId().equals(id))
                .filter(p -> product.getCategory() != null
                        && p.getCategory() != null
                        && p.getCategory().getId().equals(product.getCategory().getId()))
                .limit(4)
                .map(mapper::toCard)
                .toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return mapper.toProduct(products.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = load(id);
        apply(product, request);
        return mapper.toProduct(products.save(product));
    }

    @Transactional
    public void delete(Long id) {
        products.delete(load(id));
    }

    Product load(Long id) {
        return products.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(AuthService.sanitize(request.name()));
        product.setDescription(AuthService.sanitize(request.description()));
        product.setSubcategory(AuthService.sanitize(request.subcategory()));
        product.setTechnique(AuthService.sanitize(request.technique()));
        product.setMaterials(request.materials() == null ? new ArrayList<>() : request.materials());
        product.setDimensions(AuthService.sanitize(request.dimensions()));
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setOneOfAKind(request.oneOfAKind());
        product.setImages(request.images() == null ? new ArrayList<>() : request.images());
        product.setImageAlts(request.imageAlts() == null ? new ArrayList<>() : request.imageAlts());
        product.setTags(request.tags() == null ? new ArrayList<>() : request.tags());
        if (request.categoryId() != null) {
            Category category = categories.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (request.artisanId() != null) {
            product.setArtisan(artisans.findById(request.artisanId())
                    .orElseThrow(() -> new NotFoundException("Artisan not found")));
        }
    }
}
