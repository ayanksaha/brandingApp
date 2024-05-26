package com.lb.brandingApp.task.service;

import com.lb.brandingApp.app.utils.AppUtil;
import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.common.UserExtension;
import com.lb.brandingApp.auth.repository.TeamRepository;
import com.lb.brandingApp.auth.repository.UserRepository;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.category.repository.CategoryRepository;
import com.lb.brandingApp.category.repository.DistrictRepository;
import com.lb.brandingApp.category.repository.StateRepository;
import com.lb.brandingApp.common.data.entities.*;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import com.lb.brandingApp.common.data.enums.ImageReference;
import com.lb.brandingApp.common.data.enums.Status;
import com.lb.brandingApp.common.data.enums.TeamDescription;
import com.lb.brandingApp.common.data.models.request.AmountRequestDto;
import com.lb.brandingApp.common.mapper.CommonMapper;
import com.lb.brandingApp.common.repository.*;
import com.lb.brandingApp.product.data.entities.ProductConfig;
import com.lb.brandingApp.product.repository.ProductConfigRepository;
import com.lb.brandingApp.task.data.entities.Allotment;
import com.lb.brandingApp.task.data.entities.Assignee;
import com.lb.brandingApp.task.data.entities.Task;
import com.lb.brandingApp.task.data.models.request.TaskRequestDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import com.lb.brandingApp.task.mspper.TaskMapper;
import com.lb.brandingApp.task.repository.AllotmentRepository;
import com.lb.brandingApp.task.repository.AssigneeRepository;
import com.lb.brandingApp.task.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;
import static com.lb.brandingApp.app.utils.AppUtil.*;
import static com.lb.brandingApp.app.utils.CompressionUtil.zip;

@Slf4j
@Service
@Transactional
public class TaskService {

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
    private ImageRepository imageRepository;

    @Autowired
    private DimensionRepository dimensionRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private QuantityRepository quantityRepository;

    @Autowired
    private AmountRepository amountRepository;

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Value("${tasks.default.sort.by}")
    private String defaultSortBy;

    @Value("${tasks.default.sort.order}")
    private String defaultSortOrder;

    @Value("${default.page.size}")
    private Integer defaultPageSize;

    public PageResponseDto<TaskResponseDto> getAllTasks(Long categoryId, Long stateId, Long districtId, Integer
            pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        State state = category.getStates().stream()
                .filter(stateInDB -> stateInDB.getId().longValue() == stateId).findAny()
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
        District district = state.getDistricts().stream()
                .filter(districtInDB -> districtInDB.getId().longValue() == districtId).findAny()
                .orElseThrow(() -> new RuntimeException(DISTRICT_NOT_FOUND));
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<Task> result = taskRepository.findAllByDistrict(district, page);
        List<TaskResponseDto> response = result.stream().map(task -> taskMapper.mapTaskListResponse(task)).toList();
        return PageResponseDto.<TaskResponseDto>builder()
                .content(response)
                .metadata(PageResponseDto.PagingMetadata
                        .builder()
                        .pageSize(result.getNumberOfElements())
                        .pageNumber(result.getNumber())
                        .totalPages(result.getTotalPages())
                        .totalElements(result.getTotalElements())
                        .build())
                .build();
    }

    public PageResponseDto<TaskResponseDto> getAllTasksByTeam(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        UserExtension user = (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = user.getAuthorities().stream().map(
                GrantedAuthority::getAuthority).toList();
        if (authorities.isEmpty()) {
            throw new RuntimeException(TEAM_NOT_FOUND);
        }

        TeamDescription description = AppUtil.getTeamDescriptionByDescription(authorities.get(0));
        Team currentUserTeam = teamRepository.findByDescription(description)
                .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<Task> result = taskRepository.findAllByAllotments_CurrentAssignee_AssignedToTeam(currentUserTeam, page);
        List<TaskResponseDto> response = result.stream().map(task -> taskMapper.mapTaskListResponse(task)).toList();
        return PageResponseDto.<TaskResponseDto>builder()
                .content(response)
                .metadata(PageResponseDto.PagingMetadata
                        .builder()
                        .pageSize(result.getNumberOfElements())
                        .pageNumber(result.getNumber())
                        .totalPages(result.getTotalPages())
                        .totalElements(result.getTotalElements())
                        .build())
                .build();
//        Set<Assignee> assignees = currentUserTeam.getAssignees();
//        Set<Task> assignedTasks = assignees.stream().flatMap(assignee-> assignee.getCurrentAssignments().stream())
//                .map(Allotment::getTask).collect(Collectors.toSet());
//        List<TaskResponseDto> assignments = assignedTasks.stream().map(this::mapTaskResponse).toList();
//        return assignments.stream().sorted(Comparator.comparing(TaskResponseDto::getCreatedAt).reversed()).toList();
    }

    public PageResponseDto<TaskResponseDto> getAllTasksByUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        UserExtension user = (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<Task> result = taskRepository.findAllByAllotments_CurrentAssignee_AssignedTo(currentUser, page);
        List<TaskResponseDto> response = result.stream().map(task -> taskMapper.mapTaskListResponse(task)).toList();
        return PageResponseDto.<TaskResponseDto>builder()
                .content(response)
                .metadata(PageResponseDto.PagingMetadata
                        .builder()
                        .pageSize(result.getNumberOfElements())
                        .pageNumber(result.getNumber())
                        .totalPages(result.getTotalPages())
                        .totalElements(result.getTotalElements())
                        .build())
                .build();
//        Set<Assignee> assignees = currentUser.getAssignees();
//        Set<Task> assignedTasks = assignees.stream().flatMap(assignee-> assignee.getCurrentAssignments().stream())
//                .map(Allotment::getTask).collect(Collectors.toSet());
//        List<TaskResponseDto> assignments = assignedTasks.stream().map(this::mapTaskResponse).toList();
//        return assignments.stream().sorted(Comparator.comparing(TaskResponseDto::getCreatedAt).reversed()).toList();
    }

    public TaskResponseDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));
        UserExtension user = (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = user.getAuthorities().stream().map(
                GrantedAuthority::getAuthority).toList();
        if (authorities.isEmpty()) {
            throw new RuntimeException(TEAM_NOT_FOUND);
        }

        TeamDescription description = AppUtil.getTeamDescriptionByDescription(authorities.get(0));
        Team currentUserTeam = teamRepository.findByDescription(description)
                .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
        if (currentUserTeam.getPermissions().stream().noneMatch(
                permission -> permission.getPermissionName().equals("all_team_tasks"))) {
            task.setAllotments(task.getAllotments().stream().filter(allotment -> allotment.getCurrentAssignee()
               .getAssignedToTeam().getDescription() == currentUserTeam.getDescription()).collect(Collectors.toSet()));
        }

        return taskMapper.mapTaskDetailResponse(task);
    }

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

        AtomicReference<Double> aggregatedArea = new AtomicReference<>(0.0);
        AtomicReference<Integer> aggregatedQty = new AtomicReference<>(0);
        AtomicReference<Double> aggregatedAmt = new AtomicReference<>(0.0);
        AtomicBoolean isTaskPendingApproval = new AtomicBoolean(false);

        Set<Allotment> allotments = request.allotments().stream().map(allotmentRequestDto -> {

            Allotment allotment = new Allotment();
            Set<ImageData> referenceImages = new HashSet<>();
            allotmentRequestDto.referenceImages().forEach(
                    imageRequestDto -> {
                        ImageData image = new ImageData();
                        image.setImageData(zip(imageRequestDto.data()));
                        image.setName(imageRequestDto.name());
                        image.setReference(ImageReference.INITIAL);
                        referenceImages.add(image);
                    }
            );
            imageRepository.saveAll(referenceImages);
            allotment.setReferenceImages(referenceImages);

            Dimension dimension = commonMapper.mapDimension(
                    allotmentRequestDto.dimension().length(), allotmentRequestDto.dimension().width());
            dimensionRepository.save(dimension);
            allotment.setDimension(dimension);

            Note note = new Note();
            note.setText(allotmentRequestDto.noteText());
            allotment.setNotes(Set.of(note));

            int qtyValue = allotmentRequestDto.quantity().value();
            aggregatedQty.updateAndGet(v -> (v + qtyValue));
            Quantity qty = commonMapper.mapQuantity(qtyValue);
            quantityRepository.save(qty);
            allotment.setQuantity(qty);

            ProductConfig productConfig = productConfigRepository.findById(allotmentRequestDto.product().productId())
                    .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
            allotment.setProductConfig(productConfig);

            Set<WorkflowItem> productWorkflow = productConfig.getWorkflow().stream()
                    .sorted(Comparator.comparingInt(WorkflowItem::getSequence))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            Set<Assignee> futureAssignees = new LinkedHashSet<>();

            for (WorkflowItem item : productWorkflow) {
                Assignee futureAssignee = new Assignee();
                futureAssignee.setSequence(item.getSequence());
                futureAssignee.setAssignedToTeam(item.getTeam());
                futureAssignee.setStatus(Status.PENDING);
                futureAssignees.add(futureAssignee);
            }

            Assignee currentAssignee = futureAssignees.stream().min(Comparator.comparingInt(Assignee::getSequence))
                    .orElseThrow(() -> new RuntimeException(ASSIGNEE_CANT_BE_NULL));
            if (currentAssignee.getAssignedToTeam().getDescription() == TeamDescription.APPROVAL) {
                isTaskPendingApproval.set(true);
                currentAssignee.setStatus(Status.PENDING_APPROVAL);
                allotment.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);
            } else {
                allotment.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
            }
            assigneeRepository.save(currentAssignee);
            allotment.setCurrentAssignee(currentAssignee);

            futureAssignees.remove(currentAssignee);
            assigneeRepository.saveAll(futureAssignees);
            allotment.setFutureAssignees(futureAssignees);

            double calculatedArea = calculateArea(dimension, qtyValue);
            aggregatedArea.updateAndGet(v -> (v + calculatedArea));
            Area area = commonMapper.mapArea(calculatedArea);
            areaRepository.save(area);
            allotment.setArea(area);

            Amount unitAmt = productConfig.getAmount();
            double totalAllotmentAmt = (calculatedArea > 0) ? (unitAmt.getValue() * calculatedArea)
                    : (unitAmt.getValue() * qtyValue);
            aggregatedAmt.updateAndGet(v -> (v + totalAllotmentAmt));
            Amount amount = commonMapper.mapAmount(totalAllotmentAmt);
            amountRepository.save(amount);
            allotment.setAmount(amount);

            allotment.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);
            allotment.setCreatedBy(user);
            allotment.setModifiedBy(user);
            allotment.setCreatedAt(LocalDateTime.now());
            allotment.setLastModifiedAt(LocalDateTime.now());
            return allotment;

        }).collect(Collectors.toSet());

        if (isTaskPendingApproval.get()) {
            task.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);
        } else {
            task.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        }

        allotmentRepository.saveAll(allotments);
        task.setAllotments(allotments);

        Area taskAggregatedArea = commonMapper.mapArea(aggregatedArea.get());
        areaRepository.save(taskAggregatedArea);
        task.setAggregatedArea(taskAggregatedArea);

        Amount taskAggregatedAmt = commonMapper.mapAmount(aggregatedAmt.get());
        amountRepository.save(taskAggregatedAmt);
        task.setAggregatedAmount(taskAggregatedAmt);

        Quantity taskAggregatedQty = commonMapper.mapQuantity(aggregatedQty.get());
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
        districtAggregatedQty.setValue(districtAggregatedQty.getValue() + 1);
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
        stateAggregatedQty.setValue(stateAggregatedQty.getValue() + 1);
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
        categoryAggregatedQty.setValue(categoryAggregatedQty.getValue() + 1);
        quantityRepository.save(categoryAggregatedQty);
        category.setAggregatedQuantity(categoryAggregatedQty);
        categoryRepository.save(category);

        task.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);

        AmountRequestDto requestedRent = request.rent();
        if (Objects.nonNull(requestedRent)) {
            Amount rent = commonMapper.mapAmount(requestedRent.value());
            amountRepository.save(rent);
            task.setRent(rent);
        }

        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void updateTask(Long taskId, TaskRequestDto request) {

    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void approveAndAssignNext(Long taskId, TaskRequestDto request) {

    }
}
