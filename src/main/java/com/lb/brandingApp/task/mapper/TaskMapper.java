package com.lb.brandingApp.task.mapper;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.response.TeamResponseDto;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.category.data.models.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.models.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.models.response.StateResponseDto;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.enums.Status;
import com.lb.brandingApp.common.data.enums.TeamDescription;
import com.lb.brandingApp.common.data.models.response.*;
import com.lb.brandingApp.product.data.models.response.ProductConfigResponseDto;
import com.lb.brandingApp.task.data.entities.Allotment;
import com.lb.brandingApp.task.data.entities.Assignee;
import com.lb.brandingApp.task.data.entities.Task;
import com.lb.brandingApp.task.data.models.response.AllotmentResponseDto;
import com.lb.brandingApp.task.data.models.response.NotesResponseDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.lb.brandingApp.app.utils.CompressionUtil.unzip;

@Component
public class TaskMapper {

    @Value("${task.status.warning.period.days}")
    private int warningPeriod;

    public TaskResponseDto mapTaskDetailResponse(Task task, boolean filterTeamTasks, TeamDescription currentTeamDescription) {
        User createdBy = task.getCreatedBy();
        User lastModifiedBy = task.getLastModifiedBy();
        Set<Allotment> allotments = task.getAllotments();

        if (filterTeamTasks) {
            allotments = allotments.stream().filter(allotment ->
                            allotment.getCurrentAssignee().getAssignedToTeam().getDescription() == currentTeamDescription)
                    .collect(Collectors.toSet());
        }

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .location(task.getLocation())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .district(DistrictResponseDto.builder()
                        .name(task.getDistrict().getDistrictConfig().getName())
                        .districtId(task.getDistrict().getId())
                        .build())
                .state(StateResponseDto.builder()
                        .name(task.getDistrict().getState().getStateConfig().getName())
                        .stateId(task.getDistrict().getState().getId())
                        .build())
                .category(CategoryResponseDto.builder()
                        .name(task.getDistrict().getState().getCategory().getName())
                        .id(task.getDistrict().getState().getCategory().getId())
                        .build())
                .mobileNumber(task.getMobileNumber())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .allotments(mapAllotments(task, allotments))
                .quantity(QuantityResponseDto.builder()
                        .value(task.getAggregatedQuantity().getValue())
                        .unit(task.getAggregatedQuantity().getUom())
                        .build())
                .area(AreaResponseDto.builder()
                        .value(task.getAggregatedArea().getValue())
                        .unit(task.getAggregatedArea().getUnit())
                        .build())
                .amount(mapAmount(task.getAggregatedAmount()))
                .approvalStatus(task.getApprovalStatus())
                .rent(mapAmount(task.getRent()))
                .expiry(allotments.stream().filter(allotment -> Objects.nonNull(allotment.getExpiry()))
                        .min(Comparator.comparing(Allotment::getExpiry))
                        .map(Allotment::getExpiry).orElse(null))
                .status(mapTaskStatus(task))
                .images(task.getFinalImages().stream()
                        .map(imageData -> ImageResponseDto.builder()
                                .id(imageData.getId())
                                .image(unzip(imageData.getImageData()))
                                .name(imageData.getName())
                                .build()
                        ).toList())
                .createdAt(task.getCreatedAt())
                .createdBy(
                        UserResponseDto.builder()
                                .username(createdBy.getUsername())
                                .name(createdBy.getName())
                                .email(createdBy.getEmail())
                                .phoneNumber(createdBy.getPhoneNumber())
                                .team(createdBy.getTeam().getDescription().description())
                                .build())
                .lastModifiedAt(task.getLastModifiedAt())
                .lastModifiedBy(
                        UserResponseDto.builder()
                                .username(lastModifiedBy.getUsername())
                                .name(lastModifiedBy.getName())
                                .email(lastModifiedBy.getEmail())
                                .phoneNumber(lastModifiedBy.getPhoneNumber())
                                .team(lastModifiedBy.getTeam().getDescription().description())
                                .build())
                .build();
    }

    private List<AllotmentResponseDto> mapAllotments(Task task, Set<Allotment> allotments) {
        return allotments.stream().map(
                allotment -> AllotmentResponseDto.builder()
                        .id(allotment.getId())
                        .status((allotment.getCurrentAssignee() == null) ? Status.DONE : allotment.getCurrentAssignee().getStatus())
                        .area(AreaResponseDto.builder()
                                .unit(allotment.getArea().getUnit())
                                .value(allotment.getArea().getValue())
                                .build())
                        .dimension(DimensionResponseDto.builder()
                                .length(allotment.getDimension().getLength())
                                .width(allotment.getDimension().getWidth())
                                .unit(allotment.getDimension().getUnit())
                                .build())
                        .product(ProductConfigResponseDto.builder()
                                .unitAmount(allotment.getProductConfig().getAmount())
                                .name(allotment.getProductConfig().getName())
                                .id(allotment.getProductConfig().getId())
                                .build())
                        .notes(allotment.getNotes().stream().map(
                                note -> NotesResponseDto.builder()
                                        .text(note.getText())
                                        .build()
                        ).toList())
                        .futureTeams(allotment.getFutureAssignees().stream()
                                .sorted(Comparator.comparingInt(Assignee::getSequence))
                                .map(assignee -> mapTeamResponse(assignee.getAssignedToTeam()))
                                .toList())
                        .assignee(
                                (Objects.isNull(allotment.getCurrentAssignee()) ||
                                        Objects.isNull(allotment.getCurrentAssignee().getAssignedTo())) ? null
                                        : UserResponseDto.builder()
                                        .username(allotment.getCurrentAssignee().getAssignedTo().getUsername())
                                        .name(allotment.getCurrentAssignee().getAssignedTo().getName())
                                        .email(allotment.getCurrentAssignee().getAssignedTo().getEmail())
                                        .phoneNumber(allotment.getCurrentAssignee().getAssignedTo().getPhoneNumber())
                                        .team(allotment.getCurrentAssignee().getAssignedTo().getTeam().getDescription().description())
                                        .build())
                        .status(mapTaskStatus(task))
                        .nextTeam(mapTeamResponse(allotment.getFutureAssignees().stream()
                                .min(Comparator.comparingInt(Assignee::getSequence)).map(Assignee::getAssignedToTeam)
                                .orElse(null)))
                        .assignedTeam(Objects.isNull(allotment.getCurrentAssignee()) ? null :
                                mapTeamResponse(allotment.getCurrentAssignee().getAssignedToTeam()))
                        .quantity(QuantityResponseDto.builder()
                                .unit(allotment.getQuantity().getUom())
                                .value(allotment.getQuantity().getValue())
                                .build())
                        .amount(AmountResponseDto.builder()
                                .currency(allotment.getAmount().getCurrency())
                                .value(allotment.getAmount().getValue())
                                .build())
                        .approvalStatus(allotment.getApprovalStatus())
                        .createdAt(allotment.getCreatedAt())
                        .createdBy(UserResponseDto.builder()
                                .username(allotment.getCreatedBy().getUsername())
                                .name(allotment.getCreatedBy().getName())
                                .email(allotment.getCreatedBy().getEmail())
                                .phoneNumber(allotment.getCreatedBy().getPhoneNumber())
                                .team(allotment.getCreatedBy().getTeam().getDescription().description())
                                .build())
                        .lastModifiedAt(allotment.getLastModifiedAt())
                        .lastModifiedBy(UserResponseDto.builder()
                                .username(allotment.getModifiedBy().getUsername())
                                .name(allotment.getModifiedBy().getName())
                                .email(allotment.getModifiedBy().getEmail())
                                .phoneNumber(allotment.getModifiedBy().getPhoneNumber())
                                .team(allotment.getModifiedBy().getTeam().getDescription().description())
                                .build())
                        .images(allotment.getReferenceImages().stream().map(
                                imageData -> ImageResponseDto.builder()
                                        .id(imageData.getId())
                                        .image(unzip(imageData.getImageData()))
                                        .name(imageData.getName())
                                        .reference(imageData.getReference().name())
                                        .build()
                        ).toList())
                        .build()
        ).toList();
    }

    private List<AllotmentResponseDto> mapAllotmentsListResponse(Task task, Set<Allotment> allotments) {
        return allotments.stream().map(
                allotment -> AllotmentResponseDto.builder()
                        .id(allotment.getId())
                        .status((allotment.getCurrentAssignee() == null) ? Status.DONE : allotment.getCurrentAssignee().getStatus())
                        .area(AreaResponseDto.builder()
                                .unit(allotment.getArea().getUnit())
                                .value(allotment.getArea().getValue())
                                .build())
                        .dimension(DimensionResponseDto.builder()
                                .length(allotment.getDimension().getLength())
                                .width(allotment.getDimension().getWidth())
                                .unit(allotment.getDimension().getUnit())
                                .build())
                        .product(ProductConfigResponseDto.builder()
                                .unitAmount(allotment.getProductConfig().getAmount())
                                .name(allotment.getProductConfig().getName())
                                .id(allotment.getProductConfig().getId())
                                .build())
                        .notes(allotment.getNotes().stream().map(
                                note -> NotesResponseDto.builder()
                                        .text(note.getText())
                                        .build()
                        ).toList())
                        .status(mapTaskStatus(task))
                        .quantity(QuantityResponseDto.builder()
                                .unit(allotment.getQuantity().getUom())
                                .value(allotment.getQuantity().getValue())
                                .build())
                        .amount(mapAmount(allotment.getAmount()))
                        .approvalStatus(allotment.getApprovalStatus())
                        .build()
        ).toList();
    }

    private AmountResponseDto mapAmount(Amount amount) {
        if (Objects.nonNull(amount)) {
            return AmountResponseDto.builder()
                    .value(amount.getValue())
                    .currency(amount.getCurrency())
                    .build();
        }
        return null;
    }

    public TeamResponseDto mapTeamResponse(Team team) {
        if (team == null) return null;
        return TeamResponseDto.builder()
                .teamDescription(team.getDescription().description())
                .teamName(team.getDescription().name())
                .teamId(team.getId())
                .build();
    }

    public TaskResponseDto mapTaskListResponse(Task task) {
        User createdBy = task.getCreatedBy();
        User lastModifiedBy = task.getLastModifiedBy();
        Set<Allotment> allotments = task.getAllotments();
        Optional<Assignee> verificationAssigneeOpt = allotments.stream().flatMap(
                        allotment -> allotment.getEarlierAssignees().stream())
                .filter(assignee -> assignee.getAssignedToTeam().getDescription() == TeamDescription.VERIFICATION)
                .min(Comparator.comparing(Assignee::getEndDate));
        LocalDateTime verifiedAt = verificationAssigneeOpt.map(Assignee::getEndDate).orElse(null);
        UserResponseDto verifiedBy = verificationAssigneeOpt.map(assignee ->
                mapUserResponse(assignee.getAssignedTo())).orElse(null);

        Optional<Assignee> installationAssigneeOpt = allotments.stream().flatMap(
                        allotment -> allotment.getEarlierAssignees().stream())
                .filter(assignee -> assignee.getAssignedToTeam().getDescription() == TeamDescription.INSTALLATION)
                .min(Comparator.comparing(Assignee::getEndDate));
        LocalDateTime installedAt = installationAssigneeOpt.map(Assignee::getEndDate).orElse(null);
        UserResponseDto installedBy = installationAssigneeOpt.map(assignee ->
                mapUserResponse(assignee.getAssignedTo())).orElse(null);

        District district = task.getDistrict();
        State state = district.getState();
        Category category = state.getCategory();

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .category(CategoryResponseDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .state(StateResponseDto.builder()
                        .stateId(state.getId())
                        .name(state.getStateConfig().getName())
                        .build())
                .district(DistrictResponseDto.builder()
                        .districtId(district.getId())
                        .name(district.getDistrictConfig().getName())
                        .build())
                .location(task.getLocation())
                .endDate(task.getEndDate())
                .images(task.getFinalImages().stream()
                        .map(imageData -> ImageResponseDto.builder()
                                .name(imageData.getName())
                                .image(unzip(imageData.getImageData()))
                                .build()
                        ).toList())
                .quantity(QuantityResponseDto.builder()
                        .value(task.getAggregatedQuantity().getValue())
                        .unit(task.getAggregatedQuantity().getUom())
                        .build())
                .area(AreaResponseDto.builder()
                        .value(task.getAggregatedArea().getValue())
                        .unit(task.getAggregatedArea().getUnit())
                        .build())
                .amount(mapAmount(task.getAggregatedAmount()))
                .rent(mapAmount(task.getRent()))
                .status(mapTaskStatus(task))
                .gift(task.getGift())
                .expiry(allotments.stream().filter(allotment ->
                                Objects.nonNull(allotment.getExpiry())).min(Comparator.comparing(Allotment::getExpiry))
                        .map(Allotment::getExpiry).orElse(null))
                .verifiedAt(verifiedAt)
                .verifiedBy(verifiedBy)
                .installedAt(installedAt)
                .installedBy(installedBy)
                .allotments(mapAllotmentsListResponse(task, allotments))
                .createdAt(task.getCreatedAt())
                .createdBy(mapUserResponse(createdBy))
                .lastModifiedAt(task.getLastModifiedAt())
                .lastModifiedBy(mapUserResponse(lastModifiedBy))
                .build();
    }

    private UserResponseDto mapUserResponse(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .team(user.getTeam().getDescription().description())
                .build();
    }

    public Status mapTaskStatus(Task task) {
        switch (task.getApprovalStatus()) {
            case PENDING_APPROVAL:
                return Status.PENDING_APPROVAL;
            case APPROVED: {
                List<Assignee> currentAssignees = task.getAllotments().stream().map(Allotment::getCurrentAssignee).toList();
                if (LocalDateTime.now().minus(warningPeriod, ChronoUnit.DAYS).isAfter(task.getApprovedAt())
                        && currentAssignees.stream().anyMatch(assignee -> Objects.nonNull(assignee)
                        && (assignee.getStatus() != Status.DONE))) {
                    return Status.PENDING;
                }
                if (task.getAllotments().stream().allMatch(allotment -> allotment.getFutureAssignees().isEmpty()
                        && Objects.isNull(allotment.getCurrentAssignee()))) {
                    return Status.DONE;
                }
                return Status.IN_PROGRESS;
            }
            case REJECTED:
                return Status.REJECTED;
            default: {
                if (task.getAllotments().stream().allMatch(allotment -> Objects.nonNull(allotment.getFutureAssignees())
                        && allotment.getFutureAssignees().isEmpty()
                        && Objects.isNull(allotment.getCurrentAssignee()))) {
                    return Status.DONE;
                }
                return Status.IN_PROGRESS;
            }
        }
    }

}
