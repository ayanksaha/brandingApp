package com.lb.brandingApp.category.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.models.response.AmountResponseDto;
import com.lb.brandingApp.common.data.models.response.AreaResponseDto;
import com.lb.brandingApp.common.data.models.response.QuantityResponseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictResponseDto {

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("state_id")
    private Long stateId;

    @JsonProperty("district_config_id")
    private Long districtConfigId;

    private String name;

    @JsonProperty("aggregated_qty")
    private QuantityResponseDto aggregatedQuantity;

    @JsonProperty("aggregated_area")
    private AreaResponseDto aggregatedArea;

    @JsonProperty("aggregated_amount")
    private AmountResponseDto aggregatedAmount;

    @JsonProperty("district_id")
    private Long districtId;
}
