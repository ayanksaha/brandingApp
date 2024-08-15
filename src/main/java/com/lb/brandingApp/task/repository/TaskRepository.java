package com.lb.brandingApp.task.repository;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.task.data.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByDistrict(District district, Pageable page);

    Page<Task> findAllByAllotments_CurrentAssignee_AssignedToTeamAndAllotments_CurrentAssignee_AssignedTo(Team assignedToTeam, User assignedTo, Pageable page);

    Page<Task> findAllByAllotments_CurrentAssignee_AssignedTo(User assignedTo, Pageable page);

    Page<Task> findAllByAllotments_EarlierAssignees_AssignedTo(User assignedTo, Pageable page);
}