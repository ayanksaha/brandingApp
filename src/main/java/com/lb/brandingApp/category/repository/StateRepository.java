package com.lb.brandingApp.category.repository;

import com.lb.brandingApp.category.data.dao.Category;
import com.lb.brandingApp.category.data.dao.State;
import com.lb.brandingApp.config.data.dao.StateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByStateConfigAndCategory(StateConfig stateConfig, Category category);
}
