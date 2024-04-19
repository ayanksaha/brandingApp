package com.lb.brandingApp.config.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record DistrictConfigRequestDto (
        @NonNull @JsonProperty("district_name") String districtName
) {}