package com.lb.brandingApp.task.repository;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.task.data.entities.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssigneeRepository extends JpaRepository<Assignee, Long> {}