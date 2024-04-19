package com.lb.brandingApp.task.data.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.lb.brandingApp.common.data.models.response.*;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.config.data.models.response.NotesResponseDto;
import com.lb.brandingApp.config.data.models.response.ProductConfigResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllotmentResponseDto {

    private Long id;

    private ProductConfigResponseDto product;

    private DimensionResponseDto dimension;

    private AreaResponseDto area;

    private QuantityResponseDto quantity;

    private AmountResponseDto amount;

    @JsonProperty("approval_status")
    private ApprovalStatus approvalStatus;

    private List<NotesResponseDto> notes;

    private List<ImageResponseDto> images;

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
