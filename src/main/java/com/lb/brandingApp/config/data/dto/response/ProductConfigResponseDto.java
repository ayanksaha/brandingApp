package com.lb.brandingApp.config.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.dao.Amount;
import lombok.Builder;
import lombok.Getter;

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

}
