package com.lb.brandingApp.common.data.dao;

import com.lb.brandingApp.auth.data.dao.Team;
import com.lb.brandingApp.category.data.dao.Category;
import com.lb.brandingApp.task.data.dao.Task;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
