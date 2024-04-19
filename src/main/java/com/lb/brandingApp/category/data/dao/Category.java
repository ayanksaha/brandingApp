package com.lb.brandingApp.category.data.dao;

import com.lb.brandingApp.common.data.dao.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @OneToOne
    private Quantity aggregatedQuantity;

    @OneToOne
    private Area aggregatedArea;

    @OneToOne
    private Amount aggregatedAmount;

    @OneToMany(mappedBy = "category")
    private Set<State> states = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Set<WorkflowItem> workflow;

    @OneToOne
    private TimePeriod validity;
}
