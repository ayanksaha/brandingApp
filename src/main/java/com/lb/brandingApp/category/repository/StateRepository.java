package com.lb.brandingApp.category.repository;

import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.location.data.entities.StateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByStateConfigAndCategory(StateConfig stateConfig, Category category);
}
