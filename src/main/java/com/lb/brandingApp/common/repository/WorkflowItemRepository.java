package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.common.data.entities.WorkflowItem;
import com.lb.brandingApp.task.data.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowItemRepository extends JpaRepository<WorkflowItem, Long> {
    Optional<WorkflowItem> findByCategoryAndTaskAndTeam(Category category, Task task, Team team);
}