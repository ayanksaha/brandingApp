package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.common.data.entities.TimePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimePeriodRepository extends JpaRepository<TimePeriod, Long> {}