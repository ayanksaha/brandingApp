package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.task.data.entities.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workflow_items")
@Getter
@Setter
@NoArgsConstructor
public class WorkflowItem {

    @Id
    @GeneratedValue
    private Long id;

    private Integer itemNumber;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
