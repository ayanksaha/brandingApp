package com.lb.brandingApp.config.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StateConfigResponseDto {

    @JsonProperty("state_id")
    private Long stateId;

    @JsonProperty("state_name")
    private String stateName;
}
