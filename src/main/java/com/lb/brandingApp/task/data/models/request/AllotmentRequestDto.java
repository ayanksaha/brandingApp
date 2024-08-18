package com.lb.brandingApp.task.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.models.request.AmountRequestDto;
import com.lb.brandingApp.common.data.models.request.DimensionRequestDto;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.data.models.request.QuantityRequestDto;
import com.lb.brandingApp.product.data.models.request.ProductConfigRequestDto;

import java.util.List;

public record AllotmentRequestDto(
        Long id,
        ProductConfigRequestDto product,
        String occasion,
        String item,
        DimensionRequestDto dimension,
        QuantityRequestDto quantity,
        AmountRequestDto amount1,
        AmountRequestDto amount2,
        @JsonProperty("images") List<ImageRequestDto> referenceImages,
        @JsonProperty("invoice_images") List<ImageRequestDto> invoiceImages
) {
}