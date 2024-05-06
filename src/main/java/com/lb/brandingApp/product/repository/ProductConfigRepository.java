package com.lb.brandingApp.product.repository;

import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.product.data.entities.ProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductConfigRepository extends JpaRepository<ProductConfig, Long> {
    List<ProductConfig> findAllByCategory(Category category);

    Optional<ProductConfig> findByIdAndCategory(Long productId, Category category);
}