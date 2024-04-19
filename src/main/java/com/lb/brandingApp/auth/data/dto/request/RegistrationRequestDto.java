package com.lb.brandingApp.auth.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record RegistrationRequestDto (
        @NonNull String username,
        String name,
        @NonNull String email,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("team_id") Long teamId
) {}