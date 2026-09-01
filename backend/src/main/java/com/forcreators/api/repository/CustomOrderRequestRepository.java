package com.forcreators.api.repository;

import com.forcreators.api.domain.CustomOrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomOrderRequestRepository extends JpaRepository<CustomOrderRequest, Long> {
}
