package com.lb.brandingApp.auth.data.models.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PermissionDto {
    private String permissionName;
    private boolean httpResource;
    private String resourceUri;
    private String httpMethod;
}
