package com.lb.brandingApp.task.data.entities;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.common.data.entities.Note;
import com.lb.brandingApp.common.data.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "assignee")
@Getter
@Setter
@NoArgsConstructor
public class Assignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team assignedToTeam;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING_APPROVAL;

    @ManyToOne
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToMany(mappedBy = "earlierAssignees")
    private Set<Task> task;

    @OneToMany
    @JoinColumn(name = "assignee_id")
    private Set<Task> currentAssignments;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

}
