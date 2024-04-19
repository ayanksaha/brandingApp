package com.lb.brandingApp.category.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashSet;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDto {
    @JsonProperty("category_id")
    private Long id;
    private String name;

    @JsonProperty("aggregated_qty")
    private QuantityResponseDto aggregatedQuantity;

    @JsonProperty("aggregated_area")
    private AreaResponseDto aggregatedArea;

    @JsonProperty("aggregated_amount")
    private AmountResponseDto aggregatedAmount;

    private LinkedHashSet<WorkflowItemResponseDto> workflow;

    private TimePeriodResponseDto validity;
}
