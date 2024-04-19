package com.lb.brandingApp.category.repository;

import com.lb.brandingApp.category.data.dao.District;
import com.lb.brandingApp.category.data.dao.State;
import com.lb.brandingApp.config.data.dao.DistrictConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByDistrictConfigAndState(DistrictConfig districtConfig, State state);
}
