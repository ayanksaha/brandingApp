package com.lb.brandingApp.common.data.dto.request;

import lombok.NonNull;

public record AmountRequestDto(
        @NonNull Double value
) {
}
