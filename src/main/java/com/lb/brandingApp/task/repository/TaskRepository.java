package com.lb.brandingApp.task.repository;

import com.lb.brandingApp.task.data.dao.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {}