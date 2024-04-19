package com.lb.brandingApp.common.data.models.request;

import com.lb.brandingApp.common.data.enums.TimeUnit;

public record TimePeriodRequestDto(
        Long id,
        Integer value,
        TimeUnit unit
) {
}
