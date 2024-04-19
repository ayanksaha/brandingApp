package com.lb.brandingApp.product.repository;

import com.lb.brandingApp.product.data.entities.ProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductConfigRepository extends JpaRepository<ProductConfig, Long> {}