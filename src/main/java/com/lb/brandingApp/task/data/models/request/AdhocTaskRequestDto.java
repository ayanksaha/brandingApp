package com.lb.brandingApp.task.data.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lb.brandingApp.category.data.models.request.DistrictRequestDto;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AdhocTaskRequestDto(
        @NonNull String name,
        DistrictRequestDto district,
        String location,
        Double latitude,
        Double longitude,
        String description
) {
}
