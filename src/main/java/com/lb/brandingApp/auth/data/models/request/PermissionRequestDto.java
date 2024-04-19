package com.lb.brandingApp.auth.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record PermissionRequestDto(

        @NonNull  @JsonProperty("permission_name") String permissionName,

        @JsonProperty("is_http_resource") Boolean httpResource,

        @JsonProperty("resource_uri") String resourceUri,

        @JsonProperty("http_method") String httpMethod

) {}