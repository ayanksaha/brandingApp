package com.lb.brandingApp.product.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.category.data.models.request.WorkflowItemRequestDto;
import com.lb.brandingApp.common.data.models.request.AmountRequestDto;
import com.lb.brandingApp.common.data.models.request.TimePeriodRequestDto;

import java.util.LinkedHashSet;

public record ProductConfigRequestDto (
        @JsonProperty("product_id") Long productId,
        @JsonProperty("product") String productName,
        @JsonProperty("amount") AmountRequestDto amount,
        LinkedHashSet<WorkflowItemRequestDto> workflow,
        TimePeriodRequestDto validity
) {}