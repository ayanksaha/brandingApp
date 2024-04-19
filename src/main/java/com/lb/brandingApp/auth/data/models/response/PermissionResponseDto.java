package com.lb.brandingApp.auth.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionResponseDto {

    @JsonProperty("permission_name")
    private String permissionName;

    @JsonProperty("is_http_resource")
    private boolean httpResource;

    @JsonProperty("resource_uri")
    private String resourceUri;

    @JsonProperty("http_method")
    private String httpMethod;
}
