package com.lb.brandingApp.category.data.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.data.models.request.TimePeriodRequestDto;

import java.util.LinkedHashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryRequestDto (
        String name,
        ImageRequestDto icon,
        @JsonProperty("verification_interval") TimePeriodRequestDto verificationInterval
) {}