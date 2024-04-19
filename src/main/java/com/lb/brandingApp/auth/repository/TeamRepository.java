package com.lb.brandingApp.auth.repository;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.common.data.enums.TeamDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByDescription(TeamDescription description);
}
