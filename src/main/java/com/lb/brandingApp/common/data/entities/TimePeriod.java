package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.common.data.enums.TimeUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "timePeriod")
@Getter
@Setter
@NoArgsConstructor
public class TimePeriod {

    @Id
    @GeneratedValue
    private Long id;

    private Integer value;

    @Enumerated(EnumType.STRING)
    private TimeUnit unit = TimeUnit.DAYS;
}
