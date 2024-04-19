package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.task.data.entities.Allotment;
import com.lb.brandingApp.task.data.entities.Assignee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "notes")
@Getter
@Setter
@RequiredArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String text;

    @ManyToOne
    @JoinColumn(name = "allotment_id", nullable = false)
    private Allotment allotment;

    @OneToMany
    @JoinColumn(name = "note_id")
    private Set<Assignee> assignees;
}
