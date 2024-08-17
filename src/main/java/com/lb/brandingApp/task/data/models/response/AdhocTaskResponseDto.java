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
import com.lb.brandingApp.common.data.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdhocTaskResponseDto {

    private Long id;

    private String name;

    private String description;

    private String location;

    @JsonProperty("is_converted")
    private boolean isConverted;

    @JsonProperty("converted_task_status")
    private Status convertedTaskStatus;

    @JsonProperty("converted_task_status_description")
    private String convertedTaskStatusDescription;

    @JsonProperty("task_status")
    private Status taskStatus;

    @JsonProperty("converted_task_id")
    private Long convertedTaskId;

    private Double latitude;

    private Double longitude;

    private DistrictResponseDto district;

    private StateResponseDto state;

    private CategoryResponseDto category;

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
