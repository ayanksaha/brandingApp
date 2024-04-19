package com.lb.brandingApp.auth.data.dao;

import com.lb.brandingApp.task.data.dao.Allotment;
import com.lb.brandingApp.task.data.dao.Assignee;
import com.lb.brandingApp.task.data.dao.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 50)
    private String username;

    @Column(length = 200)
    private String name;

    @Column(length = 500)
    private String password;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    @Column
    private boolean active;

    @Column
    private boolean defaultPass;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<Assignee> assignees;

    @OneToMany
    @JoinColumn(name = "created_by")
    private Set<Task> tasksCreated;

    @OneToMany
    @JoinColumn(name = "last_modified_by")
    private Set<Task> tasksModified;

    @OneToMany
    @JoinColumn(name = "created_by")
    private Set<Allotment> allotmentsCreated;

    @OneToMany
    @JoinColumn(name = "last_modified_by")
    private Set<Task> allotmentsModified;
}
