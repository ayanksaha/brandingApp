package com.lb.brandingApp.task.service.impl;

import com.lb.brandingApp.app.utils.AppUtil;
import com.lb.brandingApp.auth.data.dao.Team;
import com.lb.brandingApp.auth.data.dao.User;
import com.lb.brandingApp.auth.data.dto.common.UserExtension;
import com.lb.brandingApp.auth.data.dto.response.TeamResponseDto;
import com.lb.brandingApp.auth.data.dto.response.UserResponseDto;
import com.lb.brandingApp.auth.repository.TeamRepository;
import com.lb.brandingApp.auth.repository.UserRepository;
import com.lb.brandingApp.category.data.dao.Category;
import com.lb.brandingApp.category.data.dao.District;
import com.lb.brandingApp.category.data.dao.State;
import com.lb.brandingApp.category.data.dto.response.*;
import com.lb.brandingApp.category.repository.CategoryRepository;
import com.lb.brandingApp.category.repository.DistrictRepository;
import com.lb.brandingApp.category.repository.StateRepository;
import com.lb.brandingApp.common.data.dao.*;
import com.lb.brandingApp.common.data.dto.response.AmountResponseDto;
import com.lb.brandingApp.common.data.dto.response.AreaResponseDto;
import com.lb.brandingApp.common.data.dto.response.QuantityResponseDto;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import com.lb.brandingApp.common.data.enums.ImageReference;
import com.lb.brandingApp.common.data.enums.Status;
import com.lb.brandingApp.common.data.enums.TeamDescription;
import com.lb.brandingApp.common.repository.*;
import com.lb.brandingApp.config.data.dao.ProductConfig;
import com.lb.brandingApp.config.data.dto.response.NotesResponseDto;
import com.lb.brandingApp.config.data.dto.response.ProductConfigResponseDto;
import com.lb.brandingApp.config.repository.ProductConfigRepository;
import com.lb.brandingApp.task.data.dao.Allotment;
import com.lb.brandingApp.task.data.dao.Assignee;
import com.lb.brandingApp.task.data.dao.Task;
import com.lb.brandingApp.task.data.dto.request.TaskRequestDto;
import com.lb.brandingApp.task.data.dto.response.AllotmentResponseDto;
import com.lb.brandingApp.common.data.dto.response.DimensionResponseDto;
import com.lb.brandingApp.common.data.dto.response.ImageResponseDto;
import com.lb.brandingApp.task.data.dto.response.TaskResponseDto;
import com.lb.brandingApp.task.repository.AllotmentRepository;
import com.lb.brandingApp.task.repository.AssigneeRepository;
import com.lb.brandingApp.task.repository.TaskRepository;
import com.lb.brandingApp.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;
import static com.lb.brandingApp.app.utils.AppUtil.*;
import static com.lb.brandingApp.app.utils.CompressionUtil.unzip;
import static com.lb.brandingApp.app.utils.CompressionUtil.zip;

@Slf4j
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AllotmentRepository allotmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private ProductConfigRepository productConfigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private WorkflowItemRepository workflowItemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DimensionRepository dimensionRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private QuantityRepository quantityRepository;

    @Autowired
    private AmountRepository amountRepository;

    @Override
    public List<TaskResponseDto> getAllTasks(Long categoryId, Long stateId, Long districtId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        State state = category.getStates().stream()
                .filter(stateInDB -> stateInDB.getId().longValue() == stateId).findAny()
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
        District district = state.getDistricts().stream()
                .filter(districtInDB -> districtInDB.getId().longValue() == districtId).findAny()
                .orElseThrow(() -> new RuntimeException(DISTRICT_NOT_FOUND));
        return district.getTasks().stream()
                .map(this::mapTaskResponse).sorted(Comparator.comparing(
                        TaskResponseDto::getCreatedAt).reversed()).toList();
    }

    @Override
    public List<TaskResponseDto> getAllTasksByTeam() {
        UserExtension user = (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<String> authorities = user.getAuthorities().stream().map(
                GrantedAuthority::getAuthority).toList();
        if (authorities.isEmpty()) {
            throw new RuntimeException(TEAM_NOT_FOUND);
        }

        TeamDescription description = AppUtil.getTeamDescriptionByDescription(authorities.get(0));
        Team userTeam = teamRepository.findByDescription(description)
                .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
        Set<Assignee> assignees = userTeam.getAssignees();
        List<TaskResponseDto> assignments = new ArrayList<>();
        for (Assignee assignee : assignees) {
            assignments.addAll(assignee.getCurrentAssignments().stream().map(this::mapTaskResponse).toList());
        }
        return assignments.stream().sorted(Comparator.comparingLong(TaskResponseDto::getId)).toList();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addTask(TaskRequestDto request) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        Task task = new Task();
        task.setName(request.name());
        task.setLocation(request.location());
        task.setMobileNumber(request.mobileNumber());

        District district = districtRepository.findById(request.district().districtId())
                .orElseThrow(() -> new RuntimeException(DISTRICT_NOT_FOUND));
        task.setDistrict(district);
        State state = district.getState();
        Category category = state.getCategory();
        Set<WorkflowItem> categoryWorkflow = category.getWorkflow();
        Set<WorkflowItem> taskWorkflow = new HashSet<>();

        for (WorkflowItem categoryWorkflowItem : categoryWorkflow) {
            Team team = categoryWorkflowItem.getTeam();
            Optional<WorkflowItem> workflowItemInDb = workflowItemRepository
                    .findByCategoryAndTaskAndTeam(null, null, team);
            WorkflowItem taskWorkflowItem;
            if (workflowItemInDb.isEmpty()) {
                taskWorkflowItem = new WorkflowItem();
                taskWorkflowItem.setTeam(team);
                taskWorkflowItem.setItemNumber(categoryWorkflowItem.getItemNumber());
            } else {
                taskWorkflowItem = workflowItemInDb.get();
            }
            taskWorkflow.add(taskWorkflowItem);
        }
        workflowItemRepository.saveAll(taskWorkflow);
        task.setWorkflow(taskWorkflow);

        Assignee currentAssignee = new Assignee();
        currentAssignee.setAssignedToTeam(categoryWorkflow.stream()
                .min(Comparator.comparingInt(WorkflowItem::getItemNumber))
                .orElseThrow(() -> new RuntimeException(WORKFLOW_CONFIG_ERROR)).getTeam());
        currentAssignee.setStatus(Status.PENDING_APPROVAL);
        assigneeRepository.save(currentAssignee);
        task.setCurrentAssignee(currentAssignee);

        AtomicReference<Double> aggregatedArea = new AtomicReference<>(0.0);
        AtomicReference<Integer> aggregatedQty = new AtomicReference<>(0);
        AtomicReference<Double> aggregatedAmt = new AtomicReference<>(0.0);

        Set<Allotment> allotments = request.allotments().stream().map(allotmentRequestDto -> {

            Allotment allotment = new Allotment();
            Set<ImageData> referenceImages = new HashSet<>();
            allotmentRequestDto.referenceImages().forEach(
                    imageRequestDto -> {
                        ImageData image = new ImageData();
                        image.setImageData(zip(imageRequestDto.data()));
                        image.setName(imageRequestDto.name());
                        image.setReference(imageRequestDto.type());
                        referenceImages.add(image);
                    }
            );
            imageRepository.saveAll(referenceImages);
            allotment.setReferenceImages(referenceImages);

            Dimension dimension = mapDimension(
                    allotmentRequestDto.dimension().length(), allotmentRequestDto.dimension().width());
            dimensionRepository.save(dimension);
            allotment.setDimension(dimension);

            allotment.setNotes(Set.of(new Note(allotmentRequestDto.noteText())));

            int qtyValue = allotmentRequestDto.quantity().value();
            aggregatedQty.updateAndGet(v -> (v + qtyValue));
            Quantity qty = mapQuantity(qtyValue);
            quantityRepository.save(qty);
            allotment.setQuantity(qty);

            ProductConfig productConfig = productConfigRepository.findById(allotmentRequestDto.product().productId())
                    .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
            allotment.setProductConfig(productConfig);

            double calculateArea = calculateArea(dimension);
            aggregatedArea.updateAndGet(v -> (v + calculateArea));
            Area area = mapArea(calculateArea);
            areaRepository.save(area);
            allotment.setArea(area);

            Amount unitAmt = productConfig.getAmount();
            double totalAllotmentAmt = (calculateArea > 0) ? (unitAmt.getValue() * calculateArea)
                    : (unitAmt.getValue() * qtyValue);
            aggregatedAmt.updateAndGet(v -> (v + totalAllotmentAmt));
            Amount amount = mapAmount(totalAllotmentAmt);
            amountRepository.save(amount);
            allotment.setAmount(amount);

            allotment.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);
            allotment.setCreatedBy(user);
            allotment.setModifiedBy(user);
            allotment.setCreatedAt(LocalDateTime.now());
            allotment.setLastModifiedAt(LocalDateTime.now());
            return allotment;

        }).collect(Collectors.toSet());

        allotmentRepository.saveAll(allotments);
        task.setAllotments(allotments);

        Area taskAggregatedArea = mapArea(aggregatedArea.get());
        areaRepository.save(taskAggregatedArea);
        task.setAggregatedArea(taskAggregatedArea);

        Amount taskAggregatedAmt = mapAmount(aggregatedAmt.get());
        amountRepository.save(taskAggregatedAmt);
        task.setAggregatedAmount(taskAggregatedAmt);

        Quantity taskAggregatedQty = mapQuantity(aggregatedQty.get());
        quantityRepository.save(taskAggregatedQty);
        task.setAggregatedQuantity(taskAggregatedQty);

        Area districtAggregatedArea = district.getAggregatedArea();
        districtAggregatedArea.setValue(districtAggregatedArea.getValue() + aggregatedArea.get());
        areaRepository.save(districtAggregatedArea);
        district.setAggregatedArea(districtAggregatedArea);

        Amount districtAggregatedAmt = district.getAggregatedAmount();
        districtAggregatedAmt.setValue(districtAggregatedAmt.getValue() + aggregatedAmt.get());
        amountRepository.save(districtAggregatedAmt);
        district.setAggregatedAmount(districtAggregatedAmt);

        Quantity districtAggregatedQty = district.getAggregatedQuantity();
        districtAggregatedQty.setValue(districtAggregatedQty.getValue() + aggregatedQty.get());
        quantityRepository.save(districtAggregatedQty);
        district.setAggregatedQuantity(districtAggregatedQty);
        districtRepository.save(district);

        Area stateAggregatedArea = state.getAggregatedArea();
        stateAggregatedArea.setValue(stateAggregatedArea.getValue() + aggregatedArea.get());
        areaRepository.save(stateAggregatedArea);
        state.setAggregatedArea(stateAggregatedArea);

        Amount stateAggregatedAmt = state.getAggregatedAmount();
        stateAggregatedAmt.setValue(stateAggregatedAmt.getValue() + aggregatedAmt.get());
        amountRepository.save(stateAggregatedAmt);
        state.setAggregatedAmount(stateAggregatedAmt);

        Quantity stateAggregatedQty = state.getAggregatedQuantity();
        stateAggregatedQty.setValue(stateAggregatedQty.getValue() + aggregatedQty.get());
        quantityRepository.save(stateAggregatedQty);
        state.setAggregatedQuantity(stateAggregatedQty);
        stateRepository.save(state);

        Area categoryAggregatedArea = category.getAggregatedArea();
        categoryAggregatedArea.setValue(categoryAggregatedArea.getValue() + aggregatedArea.get());
        areaRepository.save(categoryAggregatedArea);
        category.setAggregatedArea(categoryAggregatedArea);

        Amount categoryAggregatedAmt = category.getAggregatedAmount();
        categoryAggregatedAmt.setValue(categoryAggregatedAmt.getValue() + aggregatedAmt.get());
        amountRepository.save(categoryAggregatedAmt);
        category.setAggregatedAmount(categoryAggregatedAmt);

        Quantity categoryAggregatedQty = category.getAggregatedQuantity();
        categoryAggregatedQty.setValue(categoryAggregatedQty.getValue() + aggregatedQty.get());
        quantityRepository.save(categoryAggregatedQty);
        category.setAggregatedQuantity(categoryAggregatedQty);
        categoryRepository.save(category);

        task.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);

        Amount rent = mapAmount(request.rent().value());
        amountRepository.save(rent);
        task.setRent(rent);

        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public TaskResponseDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));
        return mapTaskDetailResponse(task);
    }

    private TaskResponseDto mapTaskDetailResponse(Task task) {
        User createdBy = task.getCreatedBy();
        User lastModifiedBy = task.getLastModifiedBy();

        List<WorkflowItemResponseDto> workflowResponse = task.getWorkflow().stream().map(
                item -> {
                    Team team = item.getTeam();
                    return WorkflowItemResponseDto.builder()
                            .teamId(team.getId())
                            .description(team.getDescription().description())
                            .name(team.getDescription().name())
                            .order(item.getItemNumber())
                            .build();
                }
        ).sorted(Comparator.comparingLong(WorkflowItemResponseDto::getOrder)).toList();

        Assignee currentAssignee = task.getCurrentAssignee();
        TeamResponseDto next = null;
        if (Objects.nonNull(currentAssignee.getAssignedToTeam())) {
            Iterator<WorkflowItemResponseDto> iterator = workflowResponse.iterator();
            while (iterator.hasNext()) {
                WorkflowItemResponseDto item = iterator.next();
                if (item.getTeamId().longValue() == currentAssignee.getAssignedToTeam().getId()
                        && iterator.hasNext()) {
                    WorkflowItemResponseDto nextItem = iterator.next();
                    next = TeamResponseDto.builder()
                            .teamId(nextItem.getTeamId())
                            .teamName(nextItem.getName())
                            .teamDescription(nextItem.getDescription())
                            .build();
                }
            }
        }

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .workflow(workflowResponse)
                .location(task.getLocation())
                .district(DistrictResponseDto.builder()
                        .name(task.getDistrict().getDistrictConfig().getName())
                        .build())
                .state(StateResponseDto.builder()
                        .name(task.getDistrict().getState().getStateConfig().getName())
                        .build())
                .category(CategoryResponseDto.builder()
                        .name(task.getDistrict().getState().getCategory().getName())
                        .build())
                .mobileNumber(task.getMobileNumber())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .allotments(
                        task.getAllotments().stream().map(
                                allotment -> AllotmentResponseDto.builder()
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
                                                .build())
                                        .notes(allotment.getNotes().stream().map(
                                                note -> NotesResponseDto.builder()
                                                        .text(note.getText())
                                                        .build()
                                        ).toList())
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
                .rent(AmountResponseDto.builder()
                        .value(task.getRent().getValue())
                        .currency(task.getRent().getCurrency())
                        .build())
                .expiry(task.getExpiry())
                .assignee(
                        Objects.isNull(currentAssignee.getAssignedTo()) ? null
                                : UserResponseDto.builder()
                                .username(currentAssignee.getAssignedTo().getUsername())
                                .name(currentAssignee.getAssignedTo().getName())
                                .email(currentAssignee.getAssignedTo().getEmail())
                                .phoneNumber(currentAssignee.getAssignedTo().getPhoneNumber())
                                .build())
                .nextTeam(next)
                .assignedTeam(TeamResponseDto.builder()
                        .teamDescription(currentAssignee.getAssignedToTeam().getDescription().description())
                        .teamName(currentAssignee.getAssignedToTeam().getDescription().name())
                        .teamId(currentAssignee.getAssignedToTeam().getId())
                        .build())
                .status(getStatus(task))
                .images(task.getReferenceImages().stream()
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

    private TaskResponseDto mapTaskResponse(Task task) {
        User createdBy = task.getCreatedBy();
        User lastModifiedBy = task.getLastModifiedBy();

        return TaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .endDate(task.getEndDate())
                .images(task.getReferenceImages().stream().filter(
                                image -> image.getReference() == ImageReference.FINAL)
                        .map(imageData -> ImageResponseDto.builder()
                                .name(imageData.getName())
                                .image(unzip(imageData.getImageData()))
                                .build()
                        ).toList())
                .status(getStatus(task))
                .expiry(task.getExpiry())
                .verifiedAt(task.getEarlierAssignees().stream().filter(
                        assignee -> assignee.getAssignedToTeam().getDescription() == TeamDescription.VERIFICATION
                ).findFirst().map(Assignee::getEndDate).orElse(null))
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

    private Status getStatus(Task task) {

        Assignee currentAssignee = task.getCurrentAssignee();
        Status status = Status.PENDING;
        if (Objects.nonNull(currentAssignee) && Objects.nonNull(currentAssignee.getAssignedTo())) {
            status = currentAssignee.getStatus();
        } else if (task.getEarlierAssignees().size() == task.getWorkflow().size()
                && task.getEarlierAssignees().stream().map(Assignee::getAssignedToTeam).collect(Collectors.toSet())
                .containsAll(task.getWorkflow().stream().map(WorkflowItem::getTeam).toList())
                && task.getEarlierAssignees().stream().allMatch(earlierAssignee -> earlierAssignee.getStatus() == Status.DONE)) {
            status = Status.DONE;
        }
        return status;
    }
}
