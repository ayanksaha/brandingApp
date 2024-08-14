package com.lb.brandingApp.task.data.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.auth.data.models.request.TeamRequestDto;
import com.lb.brandingApp.category.data.models.request.DistrictRequestDto;
import com.lb.brandingApp.common.data.models.request.AmountRequestDto;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskRequestDto (
        @NonNull String name,
        DistrictRequestDto district,
        String location,
        Double latitude,
        Double longitude,
        String gift,
        @JsonProperty("mobile_number") String mobileNumber,
        Set<AllotmentRequestDto> allotments,
        AmountRequestDto rent,
        @JsonProperty("images") List<ImageRequestDto> referenceImages,
        @JsonProperty("agreement_images") List<ImageRequestDto> agreementImages
) {}