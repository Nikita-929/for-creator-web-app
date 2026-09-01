package com.forcreators.api.service;

import com.forcreators.api.domain.Product;
import com.forcreators.api.domain.Review;
import com.forcreators.api.domain.UserAccount;
import com.forcreators.api.dto.ApiDtos.ReviewRequest;
import com.forcreators.api.dto.ApiDtos.ReviewResponse;
import com.forcreators.api.exception.BusinessException;
import com.forcreators.api.exception.NotFoundException;
import com.forcreators.api.repository.ProductRepository;
import com.forcreators.api.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final ProductRepository products;
    private final DtoMapper mapper;

    public ReviewService(ReviewRepository reviews, ProductRepository products, DtoMapper mapper) {
        this.reviews = reviews;
        this.products = products;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> forProduct(Long productId) {
        return reviews.findByProductIdOrderByCreatedAtDesc(productId).stream().map(mapper::toReview).toList();
    }

    @Transactional
    public ReviewResponse create(Long productId, UserAccount user, ReviewRequest request) {
        if (request.rating() < 1 || request.rating() > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }
        Product product = products.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.rating())
                .comment(AuthService.sanitize(request.comment()))
                .images(request.images() == null ? new ArrayList<>() : request.images())
                .build();
        return mapper.toReview(reviews.save(review));
    }
}
