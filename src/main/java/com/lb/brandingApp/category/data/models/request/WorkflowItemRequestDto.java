package com.lb.brandingApp.category.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record WorkflowItemRequestDto(
        @JsonProperty("team_id") @NonNull Long id,
        @NonNull Integer order
) {}
