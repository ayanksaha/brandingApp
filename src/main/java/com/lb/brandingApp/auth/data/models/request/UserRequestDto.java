package com.lb.brandingApp.auth.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record UserRequestDto (
        String username,
        @NonNull String password,

        @JsonProperty("new_password") String newPassword
) {}