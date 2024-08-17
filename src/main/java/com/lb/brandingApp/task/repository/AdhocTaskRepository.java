package com.lb.brandingApp.task.repository;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.task.data.entities.AdhocTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdhocTaskRepository extends JpaRepository<AdhocTask, Long> {
    Page<AdhocTask> findAllByCreatedBy(User createdBy, Pageable page);
}
