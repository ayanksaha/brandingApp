package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.common.data.enums.UOM;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "calculated_area")
@Getter
@Setter
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(scale = 2)
    private Double value;

    @Enumerated(EnumType.STRING)
    private UOM unit = UOM.SQUARE_FEET;
}
