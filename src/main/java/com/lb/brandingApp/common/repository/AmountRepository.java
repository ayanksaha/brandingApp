package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.common.data.dao.Amount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmountRepository extends JpaRepository<Amount, Long> {}