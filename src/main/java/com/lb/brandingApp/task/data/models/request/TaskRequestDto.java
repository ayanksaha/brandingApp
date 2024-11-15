package com.lb.brandingApp.task.data.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.brandingApp.auth.data.models.request.TeamRequestDto;
import com.lb.brandingApp.category.data.models.request.DistrictRequestDto;
import com.lb.brandingApp.common.data.models.request.AmountRequestDto;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import lombok.NonNull;
import lombok.experimental.Accessors;

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
        @JsonProperty("sub_name") String subName,
        @JsonProperty("mobile_number") String mobileNumber,
        Set<AllotmentRequestDto> allotments,
        AmountRequestDto rent,
        AmountRequestDto cash,
        @JsonProperty("images") List<ImageRequestDto> referenceImages,
        @JsonProperty("agreement_images") List<ImageRequestDto> agreementImages,
        @JsonProperty("set_expiry") @Accessors(fluent = true) Boolean shouldSetExpiry,
        @JsonProperty("adhoc_task_id") Long adhocTaskId
) {}