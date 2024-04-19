package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.common.data.dao.Quantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuantityRepository extends JpaRepository<Quantity, Long> {}