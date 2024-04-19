package com.lb.brandingApp.common.data.models.request;

import lombok.NonNull;

public record DimensionRequestDto (
        @NonNull Double length, @NonNull Double width
) {
}
