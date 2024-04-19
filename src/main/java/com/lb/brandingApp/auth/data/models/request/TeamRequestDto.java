package com.lb.brandingApp.auth.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.enums.TeamDescription;

import java.util.List;

public record TeamRequestDto (
        @JsonProperty("name") TeamDescription description,
        @JsonProperty("home_page_uri") String homePage,

        List<PermissionRequestDto> permissions
) {}