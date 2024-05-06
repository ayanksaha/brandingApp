package com.lb.brandingApp.category.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record StateRequestDto (
        @JsonProperty("config_id") @NonNull Long stateConfigId
) {}
