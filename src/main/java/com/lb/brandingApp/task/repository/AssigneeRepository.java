package com.lb.brandingApp.task.repository;

import com.lb.brandingApp.auth.data.dao.Team;
import com.lb.brandingApp.task.data.dao.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
    List<Assignee> findAllByAssignedToTeam(Team assignedToTeam);
}
