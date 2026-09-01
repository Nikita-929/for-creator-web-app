package com.forcreators.api.service;

import com.forcreators.api.domain.Product;
import com.forcreators.api.domain.UserAccount;
import com.forcreators.api.dto.ApiDtos.ProductCard;
import com.forcreators.api.exception.NotFoundException;
import com.forcreators.api.repository.ProductRepository;
import com.forcreators.api.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final UserAccountRepository users;
    private final ProductRepository products;
    private final DtoMapper mapper;

    public WishlistService(UserAccountRepository users, ProductRepository products, DtoMapper mapper) {
        this.users = users;
        this.products = products;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProductCard> list(UserAccount user) {
        UserAccount fresh = users.findById(user.getId()).orElseThrow();
        return fresh.getWishlist().stream().map(mapper::toCard).toList();
    }

    @Transactional
    public List<ProductCard> toggle(UserAccount user, Long productId) {
        UserAccount fresh = users.findById(user.getId()).orElseThrow();
        Product product = products.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        boolean exists = fresh.getWishlist().stream().anyMatch(p -> p.getId().equals(productId));
        if (exists) {
            fresh.getWishlist().removeIf(p -> p.getId().equals(productId));
        } else {
            fresh.getWishlist().add(product);
        }
        users.save(fresh);
        return fresh.getWishlist().stream().map(mapper::toCard).toList();
    }
}
