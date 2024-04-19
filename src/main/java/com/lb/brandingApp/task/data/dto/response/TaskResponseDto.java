package com.lb.brandingApp.task.data.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.lb.brandingApp.category.data.dto.response.*;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import com.lb.brandingApp.common.data.enums.Status;
import com.lb.brandingApp.auth.data.dto.response.TeamResponseDto;
import com.lb.brandingApp.auth.data.dto.response.UserResponseDto;
import com.lb.brandingApp.data.models.category.response.*;
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

    private List<WorkflowItemResponseDto> workflow;

    private String location;

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
    @JsonInclude
    private LocalDateTime endDate;

    private List<AllotmentResponseDto> allotments;

    private QuantityResponseDto quantity;

    private AreaResponseDto area;

    private AmountResponseDto amount;

    private ApprovalStatus approvalStatus;

    private AmountResponseDto rent;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expiry;

    private UserResponseDto assignee;

    @JsonProperty("assigned_team")
    private TeamResponseDto assignedTeam;

    @JsonProperty("next_team")
    private TeamResponseDto nextTeam;

    private Status status;

    @JsonProperty("images")
    private List<ImageResponseDto> images;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("verified_at")
    @JsonInclude
    private LocalDateTime verifiedAt;

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
