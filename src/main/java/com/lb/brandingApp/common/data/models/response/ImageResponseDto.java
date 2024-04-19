package com.lb.brandingApp.common.data.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponseDto {
    private String name;
    private String image;
}
