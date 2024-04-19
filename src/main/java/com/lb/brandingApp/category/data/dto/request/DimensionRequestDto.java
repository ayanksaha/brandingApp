package com.lb.brandingApp.category.data.dto.request;

import lombok.NonNull;

public record DimensionRequestDto (
        @NonNull Double length, @NonNull Double width
) {
}
