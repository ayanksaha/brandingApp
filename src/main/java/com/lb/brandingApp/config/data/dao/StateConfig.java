package com.lb.brandingApp.config.data.dao;

import com.lb.brandingApp.category.data.dao.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "state_config")
@Getter
@Setter
@NoArgsConstructor
public class StateConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "stateConfig")
    private Set<DistrictConfig> districts;

    @OneToMany(mappedBy = "stateConfig")
    private Set<State> states;
}
