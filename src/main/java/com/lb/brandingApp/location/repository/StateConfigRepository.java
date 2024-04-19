package com.lb.brandingApp.location.repository;

import com.lb.brandingApp.location.data.entities.StateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateConfigRepository extends JpaRepository<StateConfig, Long> {
    Optional<StateConfig> findByName(String name);
}
