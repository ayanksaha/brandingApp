package com.lb.brandingApp.task.data.dto.request;

import com.lb.brandingApp.common.data.enums.ImageReference;

public record ImageRequestDto(
        String name,
        ImageReference type,
        String data
) {}