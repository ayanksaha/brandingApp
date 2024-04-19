package com.lb.brandingApp.task.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lb.brandingApp.common.data.enums.UOM;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DimensionResponseDto {
    private Double length;
    private Double width;
    private UOM unit;
}
