package com.lb.brandingApp.task.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.models.request.DimensionRequestDto;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.data.models.request.QuantityRequestDto;
import com.lb.brandingApp.product.data.models.request.ProductConfigRequestDto;

import java.util.List;

public record AllotmentRequestDto(
        Long id,
        ProductConfigRequestDto product,
        DimensionRequestDto dimension,
        QuantityRequestDto quantity,
        @JsonProperty("images") List<ImageRequestDto> referenceImages
) {}