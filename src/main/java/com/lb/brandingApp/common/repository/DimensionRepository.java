package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.common.data.entities.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DimensionRepository extends JpaRepository<Dimension, Long> {}