package com.lb.brandingApp.task.repository;

import com.lb.brandingApp.task.data.dao.Allotment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllotmentRepository extends JpaRepository<Allotment, Long> {}