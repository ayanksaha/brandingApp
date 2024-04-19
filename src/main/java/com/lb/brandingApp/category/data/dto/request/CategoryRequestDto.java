package com.lb.brandingApp.category.data.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryRequestDto (
        String name,
        LinkedHashSet<WorkflowItemRequestDto> workflow,
        TimePeriodRequestDto validity
) {}