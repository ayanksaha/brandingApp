package com.lb.brandingApp.category.data.dao;

import com.lb.brandingApp.common.data.dao.Amount;
import com.lb.brandingApp.common.data.dao.Area;
import com.lb.brandingApp.common.data.dao.Quantity;
import com.lb.brandingApp.config.data.dao.StateConfig;
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
