package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.common.data.entities.WorkflowItem;
import com.lb.brandingApp.product.data.entities.ProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowItemRepository extends JpaRepository<WorkflowItem, Long> {
    List<WorkflowItem> findAllByProductConfigAndTeam(ProductConfig productConfig, Team team);
}