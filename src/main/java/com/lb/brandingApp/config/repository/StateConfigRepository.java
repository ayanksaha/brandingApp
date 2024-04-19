package com.lb.brandingApp.config.repository;

import com.lb.brandingApp.config.data.dao.StateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateConfigRepository extends JpaRepository<StateConfig, Long> {
    Optional<StateConfig> findByName(String name);
}
