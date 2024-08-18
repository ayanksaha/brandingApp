package com.lb.brandingApp.task.data.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.category.data.models.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.models.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.models.response.StateResponseDto;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import com.lb.brandingApp.common.data.enums.Status;
import com.lb.brandingApp.common.data.models.response.AmountResponseDto;
import com.lb.brandingApp.common.data.models.response.AreaResponseDto;
import com.lb.brandingApp.common.data.models.response.ImageResponseDto;
import com.lb.brandingApp.common.data.models.response.QuantityResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponseDto {

    private Long id;

    private String name;

    private String location;

    private Double latitude;

    private Double longitude;

    private DistrictResponseDto district;

    private StateResponseDto state;

    private CategoryResponseDto category;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    private List<AllotmentResponseDto> allotments;

    private QuantityResponseDto quantity;

    private AreaResponseDto area;

    private AmountResponseDto amount;

    private AmountResponseDto cash;

    private String gift;

    @JsonProperty("sub_name")
    private String subName;

    @JsonProperty("approval_status")
    private ApprovalStatus approvalStatus;

    private AmountResponseDto rent;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime expiry;

    private Status status;

    private List<ImageResponseDto> images;

    @JsonProperty("agreement_images")
    private List<ImageResponseDto> agreementImages;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("verified_at")
    private LocalDateTime verifiedAt;

    @JsonProperty("verified_by")
    private UserResponseDto verifiedBy;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("installed_at")
    private LocalDateTime installedAt;

    @JsonProperty("installed_by")
    private UserResponseDto installedBy;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    private UserResponseDto createdBy;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedAt;

    @JsonProperty("last_modified_by")
    private UserResponseDto lastModifiedBy;

}
