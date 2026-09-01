package com.forcreators.api.repository;

import com.forcreators.api.domain.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    List<ShopOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<ShopOrder> findByRazorpayOrderId(String razorpayOrderId);
}
