package com.lb.brandingApp.auth.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Set;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private String username;

    private String name;

    private String email;

    @JsonProperty(value = "phone_number")
    private String phoneNumber;

    @JsonProperty(value = "id_token")
    private String idToken;

    private String team;

    private Set<PermissionResponseDto> permissions;

    @JsonProperty(value = "landing_page")
    private String landingPage;

    private String message;

    @Accessors(fluent = true)
    private Boolean active;

    @JsonProperty(value = "default_password_set")
    @Accessors(fluent = true)
    private Boolean defaultPasswordSet;
}
