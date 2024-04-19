package com.lb.brandingApp.task.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.dto.request.DimensionRequestDto;
import com.lb.brandingApp.common.data.dto.request.ImageRequestDto;
import com.lb.brandingApp.common.data.dto.request.QuantityRequestDto;
import com.lb.brandingApp.config.data.dto.request.ProductConfigRequestDto;

import java.util.List;

public record AllotmentRequestDto(
        ProductConfigRequestDto product,
        DimensionRequestDto dimension,
        QuantityRequestDto quantity,
        @JsonProperty("images") List<ImageRequestDto> referenceImages,
        @JsonProperty("notes") String noteText
) {}