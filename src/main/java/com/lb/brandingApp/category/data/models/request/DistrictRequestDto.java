package com.lb.brandingApp.category.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DistrictRequestDto (
        @JsonProperty("district_config_id") Long districtConfigId,
        @JsonProperty("district_id") Long districtId
) {}
