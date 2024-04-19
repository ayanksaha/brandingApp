package com.lb.brandingApp.config.data.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.common.data.models.request.AmountRequestDto;

public record ProductConfigRequestDto (
        @JsonProperty("product_id") Long productId,
        @JsonProperty("product") String productName,
        @JsonProperty("amount") AmountRequestDto amount
) {}