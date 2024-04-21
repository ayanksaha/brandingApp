package com.lb.brandingApp.common.data.entities;

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
    @GeneratedValue
    private Long id;

    private Integer value;

    @Enumerated(EnumType.STRING)
    private UOM uom = UOM.EACH;
}
