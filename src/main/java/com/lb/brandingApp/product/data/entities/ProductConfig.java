package com.lb.brandingApp.product.data.entities;

import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.TimePeriod;
import com.lb.brandingApp.common.data.entities.WorkflowItem;
import com.lb.brandingApp.task.data.entities.Allotment;
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
    @GeneratedValue
    private Long id;

    @Column(length = 100)
    private String name;

    @OneToOne
    private Amount amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_config_id")
    private Set<WorkflowItem> workflow;

    @OneToOne
    private TimePeriod validity;

    @OneToMany(mappedBy = "productConfig")
    private Set<Allotment> allotments;
}
