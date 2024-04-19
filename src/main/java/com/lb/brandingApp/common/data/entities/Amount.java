package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.common.data.enums.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "amount")
@Getter
@Setter
@NoArgsConstructor
public class Amount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(scale = 2)
    private Double value;

    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.INR;
}
