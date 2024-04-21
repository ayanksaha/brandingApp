package com.lb.brandingApp.auth.data.entities;

import com.lb.brandingApp.common.data.entities.WorkflowItem;
import com.lb.brandingApp.common.data.enums.TeamDescription;
import com.lb.brandingApp.task.data.entities.Assignee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "team")
@NoArgsConstructor
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TeamDescription description;

    private String homePageUri;

    @OneToMany(mappedBy = "team")
    private Set<User> users;

    @ManyToMany
    @JoinTable(
            name = "team_permissions",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @OneToMany(mappedBy = "team")
    private Set<WorkflowItem> workflowItems;

    @OneToMany
    @JoinColumn(name = "team_id")
    private Set<Assignee> assignees;
}
