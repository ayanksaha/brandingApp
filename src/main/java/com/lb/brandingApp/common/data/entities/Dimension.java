package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.common.data.enums.UOM;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dimension")
@Getter
@Setter
@NoArgsConstructor
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(scale = 2)
    private Double length;

    @Column(scale = 2)
    private Double width;

    @Enumerated(EnumType.STRING)
    private UOM unit = UOM.FEET;
}
