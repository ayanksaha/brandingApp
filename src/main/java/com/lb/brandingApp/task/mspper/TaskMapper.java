package com.lb.brandingApp.task.mspper;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.response.TeamResponseDto;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.category.data.models.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.models.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.models.response.StateResponseDto;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.enums.ImageReference;
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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.lb.brandingApp.app.utils.CompressionUtil.unzip;

@Component
public class TaskMapper {

    public TaskResponseDto mapTaskDetailResponse(Task task) {
        User createdBy = task.getCreatedBy();
        User lastModifiedBy = task.getLastModifiedBy();
        Set<Allotment> allotments = task.getAllotments();

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .location(task.getLocation())
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
                .allotments(
                        allotments.stream().map(
                                allotment -> AllotmentResponseDto.builder()
                                        .status(allotment.getCurrentAssignee().getStatus())
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
                                                Objects.isNull(allotment.getCurrentAssignee().getAssignedTo()) ? null
                                                        : UserResponseDto.builder()
                                                        .username(allotment.getCurrentAssignee().getAssignedTo().getUsername())
                                                        .name(allotment.getCurrentAssignee().getAssignedTo().getName())
                                                        .email(allotment.getCurrentAssignee().getAssignedTo().getEmail())
                                                        .phoneNumber(allotment.getCurrentAssignee().getAssignedTo().getPhoneNumber())
                                                        .build())
                                        .status(mapTaskStatus(task))
                                        .nextTeam(mapTeamResponse(allotment.getFutureAssignees().stream()
                                                .min(Comparator.comparingInt(Assignee::getSequence)).map(Assignee::getAssignedToTeam)
                                                .orElse(null)))
                                        .assignedTeam(mapTeamResponse(allotment.getCurrentAssignee().getAssignedToTeam()))
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
                                                .build())
                                        .lastModifiedAt(allotment.getLastModifiedAt())
                                        .lastModifiedBy(UserResponseDto.builder()
                                                .username(allotment.getModifiedBy().getUsername())
                                                .name(allotment.getModifiedBy().getName())
                                                .email(allotment.getModifiedBy().getEmail())
                                                .phoneNumber(allotment.getModifiedBy().getPhoneNumber())
                                                .build())
                                        .images(allotment.getReferenceImages().stream().map(
                                                imageData -> ImageResponseDto.builder()
                                                        .image(unzip(imageData.getImageData()))
                                                        .name(imageData.getName())
                                                        .build()
                                        ).toList())
                                        .build()
                        ).toList()
                )
                .quantity(QuantityResponseDto.builder()
                        .value(task.getAggregatedQuantity().getValue())
                        .unit(task.getAggregatedQuantity().getUom())
                        .build())
                .area(AreaResponseDto.builder()
                        .value(task.getAggregatedArea().getValue())
                        .unit(task.getAggregatedArea().getUnit())
                        .build())
                .amount(AmountResponseDto.builder()
                        .value(task.getAggregatedAmount().getValue())
                        .currency(task.getAggregatedAmount().getCurrency())
                        .build())
                .approvalStatus(task.getApprovalStatus())
                .rent(mapRent(task.getRent()))
                .expiry(allotments.stream().min(Comparator.comparing(Allotment::getExpiry))
                        .map(Allotment::getExpiry).orElse(null))
                .status(mapTaskStatus(task))
                .images(task.getFinalImages().stream()
                        .map(imageData -> ImageResponseDto.builder()
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
                                .build())
                .lastModifiedAt(task.getLastModifiedAt())
                .lastModifiedBy(
                        UserResponseDto.builder()
                                .username(lastModifiedBy.getUsername())
                                .name(lastModifiedBy.getName())
                                .email(lastModifiedBy.getEmail())
                                .phoneNumber(lastModifiedBy.getPhoneNumber())
                                .build())
                .build();
    }

    private AmountResponseDto mapRent(Amount rent) {
        if(Objects.nonNull(rent)) {
            AmountResponseDto.builder()
                    .value(rent.getValue())
                    .currency(rent.getCurrency())
                    .build();
        }
        return null;
    }

    public TeamResponseDto mapTeamResponse(Team team) {
        if (team == null) return TeamResponseDto.builder().build();
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
        LocalDateTime verifiedAt = allotments.stream().flatMap(
                        allotment -> allotment.getEarlierAssignees().stream())
                .filter(assignee -> assignee.getAssignedToTeam().getDescription() == TeamDescription.VERIFICATION)
                .min(Comparator.comparing(Assignee::getEndDate)).map(Assignee::getEndDate).orElse(null);

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .endDate(task.getEndDate())
                .images(task.getFinalImages().stream()
                        .map(imageData -> ImageResponseDto.builder()
                                .name(imageData.getName())
                                .image(unzip(imageData.getImageData()))
                                .build()
                        ).toList())
                .status(mapTaskStatus(task))
                .expiry(allotments.stream().min(Comparator.comparing(Allotment::getExpiry))
                        .map(Allotment::getExpiry).orElse(null))
                .verifiedAt(verifiedAt)
                .createdAt(task.getCreatedAt())
                .createdBy(
                        UserResponseDto.builder()
                                .username(createdBy.getUsername())
                                .name(createdBy.getName())
                                .email(createdBy.getEmail())
                                .phoneNumber(createdBy.getPhoneNumber())
                                .build())
                .lastModifiedAt(task.getLastModifiedAt())
                .lastModifiedBy(
                        UserResponseDto.builder()
                                .username(lastModifiedBy.getUsername())
                                .name(lastModifiedBy.getName())
                                .email(lastModifiedBy.getEmail())
                                .phoneNumber(lastModifiedBy.getPhoneNumber())
                                .build())
                .build();
    }

    private Status mapTaskStatus(Task task) {
        switch (task.getApprovalStatus()) {
            case PENDING_APPROVAL:
                return Status.PENDING_APPROVAL;
            case APPROVED: {
                List<Assignee> currentAssignees = task.getAllotments().stream().map(Allotment::getCurrentAssignee).toList();
                if (LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).isAfter(task.getApprovedAt())
                        && currentAssignees.stream().anyMatch(assignee -> (assignee.getStatus() != Status.DONE))) {
                    return Status.PENDING;
                }
                if (currentAssignees.stream().allMatch(assignee -> (assignee.getStatus() == Status.DONE))) {
                    return Status.DONE;
                }
                return Status.IN_PROGRESS;
            }
            case REJECTED:
                return Status.REJECTED;
            default: {
                List<Assignee> currentAssignees = task.getAllotments().stream().map(Allotment::getCurrentAssignee).toList();
                if (currentAssignees.stream().allMatch(assignee -> (assignee.getStatus() == Status.DONE))) {
                    return Status.DONE;
                }
                return Status.IN_PROGRESS;
            }
        }
    }

}
