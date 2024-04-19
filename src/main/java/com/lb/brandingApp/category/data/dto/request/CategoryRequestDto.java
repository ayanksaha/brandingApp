package com.lb.brandingApp.category.data.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lb.brandingApp.common.data.dto.request.TimePeriodRequestDto;

import java.util.LinkedHashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryRequestDto (
        String name,
        LinkedHashSet<WorkflowItemRequestDto> workflow,
        TimePeriodRequestDto validity
) {}