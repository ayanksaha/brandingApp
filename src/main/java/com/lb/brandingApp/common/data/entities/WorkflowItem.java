package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.product.data.entities.ProductConfig;
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

    @Column(name = "item_sequence")
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "product_config_id")
    private ProductConfig productConfig;

}
