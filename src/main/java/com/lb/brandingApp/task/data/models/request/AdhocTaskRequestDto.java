package com.lb.brandingApp.task.data.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.category.data.models.request.DistrictRequestDto;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import lombok.NonNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AdhocTaskRequestDto(
        @NonNull String name,
        DistrictRequestDto district,
        String location,
        Double latitude,
        Double longitude,
        String description,
        @JsonProperty("reference_images") List<ImageRequestDto> referenceImages
) {
}
