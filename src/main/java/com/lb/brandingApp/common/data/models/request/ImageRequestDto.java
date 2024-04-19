package com.lb.brandingApp.common.data.models.request;

import com.lb.brandingApp.common.data.enums.ImageReference;

public record ImageRequestDto(
        String name,
        ImageReference type,
        String data
) {}