package com.lb.brandingApp.config.data.dao;

import com.lb.brandingApp.common.data.dao.Amount;
import com.lb.brandingApp.task.data.dao.Allotment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "product_config")
@Getter
@Setter
@NoArgsConstructor
public class ProductConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @OneToOne
    private Amount amount;

    @OneToMany(mappedBy = "productConfig")
    private Set<Allotment> allotments;
}
