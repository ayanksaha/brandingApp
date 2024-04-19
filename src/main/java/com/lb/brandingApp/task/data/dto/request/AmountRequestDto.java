package com.lb.brandingApp.task.data.dto.request;

import lombok.NonNull;

public record AmountRequestDto(
        @NonNull Double value
) {
}
