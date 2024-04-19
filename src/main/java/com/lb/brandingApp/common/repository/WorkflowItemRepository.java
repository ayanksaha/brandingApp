package com.lb.brandingApp.common.repository;

import com.lb.brandingApp.auth.data.dao.Team;
import com.lb.brandingApp.category.data.dao.Category;
import com.lb.brandingApp.common.data.dao.WorkflowItem;
import com.lb.brandingApp.task.data.dao.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowItemRepository extends JpaRepository<WorkflowItem, Long> {
    Optional<WorkflowItem> findByCategoryAndTaskAndTeam(Category category, Task task, Team team);
}