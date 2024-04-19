package com.lb.brandingApp.category.data.entities;

import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.Area;
import com.lb.brandingApp.common.data.entities.Quantity;
import com.lb.brandingApp.location.data.entities.StateConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "state")
@Getter
@Setter
@NoArgsConstructor
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_config_id", nullable = false)
    private StateConfig stateConfig;

    @OneToOne
    private Quantity aggregatedQuantity;

    @OneToOne
    private Area aggregatedArea;

    @OneToOne
    private Amount aggregatedAmount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "state")
    private Set<District> districts;
}
