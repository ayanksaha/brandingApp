package com.lb.brandingApp.category.data.entities;


import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.Area;
import com.lb.brandingApp.common.data.entities.Quantity;
import com.lb.brandingApp.config.data.entities.DistrictConfig;
import com.lb.brandingApp.task.data.entities.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "district")
@Getter
@Setter
@NoArgsConstructor
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "district_config_id", nullable = false)
    private DistrictConfig districtConfig;

    @OneToOne
    private Quantity aggregatedQuantity;

    @OneToOne
    private Area aggregatedArea;

    @OneToOne
    private Amount aggregatedAmount;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @OneToMany(mappedBy = "district")
    private Set<Task> tasks;
}
