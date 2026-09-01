package com.forcreators.api.service;

import com.forcreators.api.domain.*;
import com.forcreators.api.dto.ApiDtos.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public UserResponse toUser(UserAccount user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public CategoryResponse toCategory(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getParent() == null ? null : category.getParent().getId()
        );
    }

    public ProductCard toCard(Product product) {
        boolean soldOut = product.getStockQuantity() == null || product.getStockQuantity() <= 0;
        String image = product.getImages().isEmpty() ? null : product.getImages().get(0);
        String alt = product.getImageAlts().isEmpty() ? product.getName() : product.getImageAlts().get(0);
        return new ProductCard(
                product.getId(),
                product.getName(),
                image,
                alt,
                product.getPrice(),
                product.getStockQuantity(),
                product.isOneOfAKind(),
                soldOut,
                product.getTechnique(),
                product.getArtisan() == null ? null : product.getArtisan().getId(),
                product.getArtisan() == null ? null : product.getArtisan().getName(),
                product.getCategory() == null ? null : product.getCategory().getType()
        );
    }

    public ProductResponse toProduct(Product product) {
        boolean soldOut = product.getStockQuantity() == null || product.getStockQuantity() <= 0;
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory() == null ? null : product.getCategory().getId(),
                product.getCategory() == null ? null : product.getCategory().getName(),
                product.getCategory() == null ? null : product.getCategory().getType(),
                product.getSubcategory(),
                product.getTechnique(),
                product.getMaterials(),
                product.getDimensions(),
                product.getPrice(),
                product.getStockQuantity(),
                product.isOneOfAKind(),
                soldOut,
                product.getImages(),
                product.getImageAlts(),
                product.getArtisan() == null ? null : product.getArtisan().getId(),
                product.getArtisan() == null ? null : product.getArtisan().getName(),
                product.getTags(),
                product.getCreatedAt()
        );
    }

    public ArtisanSummary toArtisanSummary(Artisan artisan) {
        return new ArtisanSummary(artisan.getId(), artisan.getName(), artisan.getPhoto(), artisan.getWorkshopLocation());
    }

    public ArtisanResponse toArtisan(Artisan artisan, List<Product> products) {
        return new ArtisanResponse(
                artisan.getId(),
                artisan.getName(),
                artisan.getBio(),
                artisan.getPhoto(),
                artisan.getWorkshopLocation(),
                artisan.getProcessDescription(),
                products.stream().map(this::toCard).collect(Collectors.toList())
        );
    }

    public OrderResponse toOrder(ShopOrder order, String razorpayKeyId) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProduct() == null ? null : i.getProduct().getId(),
                        i.getProductName(),
                        i.getQuantity(),
                        i.getUnitPrice()
                ))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTotal(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getShippingAddress(),
                order.getRazorpayOrderId(),
                razorpayKeyId,
                items,
                order.getCreatedAt()
        );
    }

    public ReviewResponse toReview(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getComment(),
                review.getImages(),
                review.getCreatedAt()
        );
    }

    public CustomOrderResponse toCustom(CustomOrderRequest request) {
        return new CustomOrderResponse(
                request.getId(),
                request.getName(),
                request.getContact(),
                request.getDescription(),
                request.getBudgetRange(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }

    public JournalResponse toJournal(JournalPost post) {
        return new JournalResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getExcerpt(),
                post.getBody(),
                post.getCoverImage(),
                post.getCreatedAt()
        );
    }
}
