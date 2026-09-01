package com.forcreators.api.dto;

import com.forcreators.api.domain.CategoryType;
import com.forcreators.api.domain.OrderStatus;
import com.forcreators.api.domain.PaymentStatus;
import com.forcreators.api.domain.RequestStatus;
import com.forcreators.api.domain.Role;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {

    private ApiDtos() {
    }

    public record AuthRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            String name
    ) {
    }

    public record AuthResponse(String token, UserResponse user) {
    }

    public record UserResponse(Long id, String email, String name, Role role) {
    }

    public record CategoryResponse(Long id, String name, CategoryType type, Long parentId) {
    }

    public record CategoryRequest(
            @NotBlank String name,
            @NotNull CategoryType type,
            Long parentId
    ) {
    }

    public record ArtisanSummary(Long id, String name, String photo, String workshopLocation) {
    }

    public record ArtisanResponse(
            Long id,
            String name,
            String bio,
            String photo,
            String workshopLocation,
            String processDescription,
            List<ProductCard> products
    ) {
    }

    public record ArtisanRequest(
            @NotBlank String name,
            String bio,
            String photo,
            String workshopLocation,
            String processDescription
    ) {
    }

    public record ProductCard(
            Long id,
            String name,
            String image,
            String imageAlt,
            BigDecimal price,
            Integer stockQuantity,
            boolean oneOfAKind,
            boolean soldOut,
            String technique,
            Long artisanId,
            String artisanName,
            CategoryType categoryType
    ) {
    }

    public record ProductResponse(
            Long id,
            String name,
            String description,
            Long categoryId,
            String categoryName,
            CategoryType categoryType,
            String subcategory,
            String technique,
            List<String> materials,
            String dimensions,
            BigDecimal price,
            Integer stockQuantity,
            boolean oneOfAKind,
            boolean soldOut,
            List<String> images,
            List<String> imageAlts,
            Long artisanId,
            String artisanName,
            List<String> tags,
            Instant createdAt
    ) {
    }

    public record ProductRequest(
            @NotBlank String name,
            String description,
            Long categoryId,
            String subcategory,
            String technique,
            List<String> materials,
            String dimensions,
            @NotNull @DecimalMin("0.0") BigDecimal price,
            @NotNull @Min(0) Integer stockQuantity,
            boolean oneOfAKind,
            List<String> images,
            List<String> imageAlts,
            Long artisanId,
            List<String> tags
    ) {
    }

    public record CartItemRequest(@NotNull Long productId, @Min(1) int quantity) {
    }

    public record CheckoutRequest(
            @NotEmpty List<CartItemRequest> items,
            @NotBlank String shippingAddress,
            String guestEmail,
            String guestName
    ) {
    }

    public record OrderItemResponse(Long productId, String productName, int quantity, BigDecimal unitPrice) {
    }

    public record OrderResponse(
            Long id,
            BigDecimal subtotal,
            BigDecimal shippingFee,
            BigDecimal total,
            OrderStatus status,
            PaymentStatus paymentStatus,
            String shippingAddress,
            String razorpayOrderId,
            String razorpayKeyId,
            List<OrderItemResponse> items,
            Instant createdAt
    ) {
    }

    public record PaymentVerifyRequest(
            @NotBlank String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {
    }

    public record ReviewResponse(
            Long id,
            Long productId,
            String userName,
            int rating,
            String comment,
            List<String> images,
            Instant createdAt
    ) {
    }

    public record ReviewRequest(
            @Min(1) @Max(5) int rating,
            String comment,
            List<String> images
    ) {
    }

    public record CustomOrderRequestDto(
            @NotBlank String name,
            @NotBlank String contact,
            @NotBlank String description,
            String budgetRange
    ) {
    }

    public record CustomOrderResponse(
            Long id,
            String name,
            String contact,
            String description,
            String budgetRange,
            RequestStatus status,
            Instant createdAt
    ) {
    }

    public record ContactRequest(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotBlank String message
    ) {
    }

    public record NewsletterRequest(@Email @NotBlank String email) {
    }

    public record JournalResponse(
            Long id,
            String title,
            String slug,
            String excerpt,
            String body,
            String coverImage,
            Instant createdAt
    ) {
    }

    public record JournalRequest(
            @NotBlank String title,
            String slug,
            String excerpt,
            String body,
            String coverImage
    ) {
    }

    public record MessageResponse(String message) {
    }
}
