package com.lb.brandingApp.product.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.category.data.models.response.WorkflowItemResponseDto;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.models.response.TimePeriodResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashSet;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductConfigResponseDto {
    @JsonProperty("product_id")
    private Long id;

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("unit_amount")
    private Amount unitAmount;

    private LinkedHashSet<WorkflowItemResponseDto> workflow;

    private TimePeriodResponseDto validity;

}
