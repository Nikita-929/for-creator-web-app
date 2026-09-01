package com.forcreators.api.repository;

import com.forcreators.api.domain.Category;
import com.forcreators.api.domain.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(CategoryType type);
    List<Category> findByParentIsNull();
}
