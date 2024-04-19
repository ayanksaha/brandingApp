package com.lb.brandingApp.config.repository;

import com.lb.brandingApp.config.data.dao.ProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductConfigRepository extends JpaRepository<ProductConfig, Long> {}