package com.lb.brandingApp.category.data.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.data.models.request.TimePeriodRequestDto;

import java.util.LinkedHashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryRequestDto (
        String name,
        LinkedHashSet<WorkflowItemRequestDto> workflow,
        TimePeriodRequestDto validity,
        ImageRequestDto icon
) {}