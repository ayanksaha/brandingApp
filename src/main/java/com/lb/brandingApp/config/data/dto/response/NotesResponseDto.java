package com.lb.brandingApp.config.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotesResponseDto {
    private final String text;
}
