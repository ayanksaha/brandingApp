package com.lb.brandingApp.common.data.models.request;

import lombok.NonNull;

public record AmountRequestDto(
        @NonNull Double value
) {
}