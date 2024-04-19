package com.lb.brandingApp.config.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record StateConfigRequestDto (
        @NonNull @JsonProperty("state_name") String stateName
) {}