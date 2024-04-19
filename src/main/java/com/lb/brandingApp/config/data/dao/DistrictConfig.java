package com.lb.brandingApp.config.data.dao;


import com.lb.brandingApp.category.data.dao.District;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "district_config")
@Getter
@Setter
@NoArgsConstructor
public class DistrictConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "state_config_id", nullable = false)
    private StateConfig stateConfig;

    @OneToMany(mappedBy = "districtConfig")
    private Set<District> districts;
}
