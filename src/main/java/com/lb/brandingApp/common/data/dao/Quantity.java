package com.lb.brandingApp.common.data.dao;

import com.lb.brandingApp.common.data.enums.UOM;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quantity")
@Getter
@Setter
@NoArgsConstructor
public class Quantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;

    @Enumerated(EnumType.STRING)
    private UOM uom = UOM.EACH;
}
