package com.lb.brandingApp.category.repository;

import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.projections.CategorySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<CategorySummary> findAllByNameContaining(String name, Pageable pageable);
}