package com.lb.brandingApp.category.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowItemResponseDto {

    private Integer order;

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_description")
    private String description;

    @JsonProperty("team_name")
    private String name;
}
