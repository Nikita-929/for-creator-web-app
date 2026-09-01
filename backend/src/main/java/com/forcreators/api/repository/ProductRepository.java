package com.forcreators.api.repository;

import com.forcreators.api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByArtisanId(Long artisanId);
    List<Product> findTop8ByOrderByCreatedAtDesc();
}
