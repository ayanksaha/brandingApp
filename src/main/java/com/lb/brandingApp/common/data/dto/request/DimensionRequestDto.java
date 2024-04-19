package com.lb.brandingApp.common.data.dto.request;

import lombok.NonNull;

public record DimensionRequestDto (
        @NonNull Double length, @NonNull Double width
) {
}
