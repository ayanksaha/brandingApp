package com.lb.brandingApp.category.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record StateRequestDto (
        @JsonProperty("state_id") @NonNull Long stateId
) {}
