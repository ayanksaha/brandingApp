package com.lb.brandingApp.common.data.models.request;

public record ImageRequestDto(
        Long id,
        String name,
        String reference,
        String data
) {}