package com.lb.brandingApp.config.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictConfigResponseDto {

    @JsonProperty("district_id")
    private Long districtId;

    @JsonProperty("district_name")
    private String districtName;

    @JsonProperty("state_id")
    private Long stateId;
}
