package com.forcreators.api.repository;

import com.forcreators.api.domain.CategoryType;
import com.forcreators.api.domain.Product;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> withFilters(
            Long categoryId,
            CategoryType type,
            String material,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String technique,
            Long artisanId,
            String q
    ) {
        return Specification.where(categoryEquals(categoryId))
                .and(typeEquals(type))
                .and(materialContains(material))
                .and(minPrice(minPrice))
                .and(maxPrice(maxPrice))
                .and(techniqueEquals(technique))
                .and(artisanEquals(artisanId))
                .and(fullText(q));
    }

    private static Specification<Product> categoryEquals(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Product> typeEquals(CategoryType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("category").get("type"), type);
    }

    private static Specification<Product> materialContains(String material) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(material)) {
                return null;
            }
            query.distinct(true);
            return cb.equal(cb.lower(root.join("materials").as(String.class)), material.toLowerCase());
        };
    }

    private static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<Product> techniqueEquals(String technique) {
        return (root, query, cb) -> !StringUtils.hasText(technique)
                ? null
                : cb.equal(cb.lower(root.get("technique")), technique.toLowerCase());
    }

    private static Specification<Product> artisanEquals(Long artisanId) {
        return (root, query, cb) -> artisanId == null ? null : cb.equal(root.get("artisan").get("id"), artisanId);
    }

    private static Specification<Product> fullText(String q) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(q)) {
                return null;
            }
            String like = "%" + q.toLowerCase() + "%";
            query.distinct(true);
            jakarta.persistence.criteria.Expression<String> tag = root.join("tags", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(root.get("technique")), like),
                    cb.like(cb.lower(tag), like)
            );
        };
    }
}
