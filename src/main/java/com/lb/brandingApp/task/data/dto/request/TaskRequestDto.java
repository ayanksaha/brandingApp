package com.lb.brandingApp.task.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.category.data.dto.request.DistrictRequestDto;
import lombok.NonNull;

import java.util.Set;

public record TaskRequestDto (
        @NonNull String name,
        DistrictRequestDto district,
        String location,
        @JsonProperty("mobile_number") String mobileNumber,
        Set<AllotmentRequestDto> allotments,
        AmountRequestDto rent
) {}