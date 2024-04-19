package com.lb.brandingApp.auth.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamResponseDto {
    @JsonProperty(value = "team_id")
    private final Long teamId;

    @JsonProperty(value = "team_name")
    private final String teamName;

    @JsonProperty(value = "team_description")
    private final String teamDescription;

    private Set<PermissionResponseDto> permissions;
}
