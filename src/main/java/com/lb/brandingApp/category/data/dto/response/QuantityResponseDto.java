package com.lb.brandingApp.category.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lb.brandingApp.common.data.enums.UOM;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuantityResponseDto {
    private Integer value;
    private UOM unit;
}
