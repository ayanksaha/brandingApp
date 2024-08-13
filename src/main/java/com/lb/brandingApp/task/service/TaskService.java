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
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.mapper.CommonMapper;
import com.lb.brandingApp.common.repository.*;
import com.lb.brandingApp.product.data.entities.ProductConfig;
import com.lb.brandingApp.product.repository.ProductConfigRepository;
import com.lb.brandingApp.task.data.entities.Allotment;
import com.lb.brandingApp.task.data.entities.Assignee;
import com.lb.brandingApp.task.data.entities.Task;
import com.lb.brandingApp.task.data.models.request.AllotmentRequestDto;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        return mapTaskResponseDto(currentUserTeam, task);
    }

    public TaskResponseDto mapTaskResponseDto(Team currentUserTeam, Task task) {
        boolean filterTeamTasks = currentUserTeam.getPermissions().stream().noneMatch(
                permission -> permission.getPermissionName().equals(ALL_TASK_PERMISSION));
        return taskMapper.mapTaskDetailResponse(task, filterTeamTasks, currentUserTeam.getDescription());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addTask(TaskRequestDto request, boolean isRenewRequest, Long originalTaskId) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        final Task task = new Task();
        String taskName;
        if (isRenewRequest) {
            final Task originalTask = taskRepository.findById(originalTaskId)
                    .orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));
            task.setRenewedFrom(originalTask);
            originalTask.setRenewed(true);
            String originalTaskName = originalTask.getName();
            originalTaskName = originalTaskName.substring(0,
                    originalTaskName.contains(" - renewed - ") ?
                            originalTaskName.indexOf(" - renewed - ") : originalTaskName.length());
            taskName = originalTaskName + " - renewed - " +
                    DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now().toLocalDate());
        } else {
            taskName = request.name();
        }

        task.setName(taskName);
        task.setGift(request.gift());
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
            saveAllotment(user, allotment, allotmentRequestDto, aggregatedArea, aggregatedQty,
                    aggregatedAmt, isTaskPendingApproval, isRenewRequest, !isRenewRequest);
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
        BigDecimal districtAreaValue = BigDecimal.valueOf(districtAggregatedArea.getValue() + aggregatedArea.get())
                .setScale(2, RoundingMode.HALF_UP);
        districtAggregatedArea.setValue(districtAreaValue.doubleValue());
        areaRepository.save(districtAggregatedArea);
        district.setAggregatedArea(districtAggregatedArea);

        Amount districtAggregatedAmt = district.getAggregatedAmount();
        BigDecimal districtAmountValue = BigDecimal.valueOf(districtAggregatedAmt.getValue() + aggregatedAmt.get())
                .setScale(2, RoundingMode.HALF_UP);
        districtAggregatedAmt.setValue(districtAmountValue.doubleValue());
        amountRepository.save(districtAggregatedAmt);
        district.setAggregatedAmount(districtAggregatedAmt);

        Quantity districtAggregatedQty = district.getAggregatedQuantity();
        districtAggregatedQty.setValue(districtAggregatedQty.getValue() + 1);
        quantityRepository.save(districtAggregatedQty);
        district.setAggregatedQuantity(districtAggregatedQty);

        Area stateAggregatedArea = state.getAggregatedArea();
        BigDecimal stateAreaValue = BigDecimal.valueOf(stateAggregatedArea.getValue() + aggregatedArea.get())
                .setScale(2, RoundingMode.HALF_UP);
        stateAggregatedArea.setValue(stateAreaValue.doubleValue());
        areaRepository.save(stateAggregatedArea);
        state.setAggregatedArea(stateAggregatedArea);

        Amount stateAggregatedAmt = state.getAggregatedAmount();
        BigDecimal stateAmountValue = BigDecimal.valueOf(stateAggregatedAmt.getValue() + aggregatedAmt.get())
                .setScale(2, RoundingMode.HALF_UP);
        stateAggregatedAmt.setValue(stateAmountValue.doubleValue());
        amountRepository.save(stateAggregatedAmt);
        state.setAggregatedAmount(stateAggregatedAmt);

        Quantity stateAggregatedQty = state.getAggregatedQuantity();
        stateAggregatedQty.setValue(stateAggregatedQty.getValue() + 1);
        quantityRepository.save(stateAggregatedQty);
        state.setAggregatedQuantity(stateAggregatedQty);

        Area categoryAggregatedArea = category.getAggregatedArea();
        BigDecimal categoryAreaValue = BigDecimal.valueOf(categoryAggregatedArea.getValue() + aggregatedArea.get())
                .setScale(2, RoundingMode.HALF_UP);
        categoryAggregatedArea.setValue(categoryAreaValue.doubleValue());
        areaRepository.save(categoryAggregatedArea);
        category.setAggregatedArea(categoryAggregatedArea);

        Amount categoryAggregatedAmt = category.getAggregatedAmount();
        BigDecimal categoryAmountValue = BigDecimal.valueOf(categoryAggregatedAmt.getValue() + aggregatedAmt.get())
                .setScale(2, RoundingMode.HALF_UP);
        categoryAggregatedAmt.setValue(categoryAmountValue.doubleValue());
        amountRepository.save(categoryAggregatedAmt);
        category.setAggregatedAmount(categoryAggregatedAmt);

        Quantity categoryAggregatedQty = category.getAggregatedQuantity();
        categoryAggregatedQty.setValue(categoryAggregatedQty.getValue() + 1);
        quantityRepository.save(categoryAggregatedQty);
        category.setAggregatedQuantity(categoryAggregatedQty);

        AmountRequestDto requestedRent = request.rent();
        if (Objects.nonNull(requestedRent)) {
            Amount rent = commonMapper.mapAmount(requestedRent.value());
            amountRepository.save(rent);
            task.setRent(rent);
        }

        List<ImageRequestDto> images = request.agreementImages();
        Set<ImageData> agreementImages = new HashSet<>();
        if (Objects.nonNull(images)) {
            images.forEach(
                    imageRequestDto -> {
                        ImageData image = new ImageData();
                        image.setImageData(zip(imageRequestDto.data()));
                        image.setName(imageRequestDto.name());
                        image.setReference(ImageReference.AGREEMENT);
                        agreementImages.add(image);
                    }
            );
            imageRepository.saveAll(agreementImages);
        }

        task.setAgreementImages(agreementImages);
        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void updateTask(Long taskId, TaskRequestDto request) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));
        task.setName(request.name());
        task.setLocation(request.location());
        task.setMobileNumber(request.mobileNumber());

        District district = task.getDistrict();
        State state = district.getState();
        Category category = state.getCategory();

        AtomicReference<Double> aggregatedArea = new AtomicReference<>(0.0);
        AtomicReference<Integer> aggregatedQty = new AtomicReference<>(0);
        AtomicReference<Double> aggregatedAmt = new AtomicReference<>(0.0);
        AtomicBoolean isTaskPendingApproval = new AtomicBoolean(false);

        Set<Allotment> allotments = request.allotments().stream().map(allotmentRequestDto -> {
            Allotment allotment = task.getAllotments().stream().filter(element ->
                    element.getId().longValue() == allotmentRequestDto.id()).findFirst().orElse(new Allotment());
            saveAllotment(user, allotment, allotmentRequestDto, aggregatedArea, aggregatedQty,
                    aggregatedAmt, isTaskPendingApproval, false, false);

            allotment.setModifiedBy(user);
            allotment.setLastModifiedAt(LocalDateTime.now());

            if (allotment.getId() == null) {
                allotment.setCreatedBy(user);
                allotment.setCreatedAt(LocalDateTime.now());
            }
            return allotment;
        }).collect(Collectors.toSet());

        if (isTaskPendingApproval.get()) {
            task.setApprovalStatus(ApprovalStatus.PENDING_APPROVAL);
        } else {
            task.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        }

        task.setAllotments(allotments);

        Area taskAggregatedArea = task.getAggregatedArea();
        double oldTaskArea = taskAggregatedArea.getValue();
        taskAggregatedArea.setValue(aggregatedArea.get());
        task.setAggregatedArea(taskAggregatedArea);

        Amount taskAggregatedAmt = task.getAggregatedAmount();
        double oldTaskAmount = taskAggregatedAmt.getValue();
        taskAggregatedAmt.setValue(aggregatedAmt.get());
        task.setAggregatedAmount(taskAggregatedAmt);

        Quantity taskAggregatedQty = task.getAggregatedQuantity();
        taskAggregatedQty.setValue(aggregatedQty.get());
        task.setAggregatedQuantity(taskAggregatedQty);

        Area districtAggregatedArea = district.getAggregatedArea();
        BigDecimal districtAreaValue = BigDecimal.valueOf(
                        districtAggregatedArea.getValue() - oldTaskArea + aggregatedArea.get())
                .setScale(2, RoundingMode.HALF_UP);
        districtAggregatedArea.setValue(districtAreaValue.doubleValue());
        district.setAggregatedArea(districtAggregatedArea);

        Amount districtAggregatedAmt = district.getAggregatedAmount();
        BigDecimal districtAmtValue = BigDecimal.valueOf(
                        districtAggregatedAmt.getValue() - oldTaskAmount + aggregatedAmt.get())
                .setScale(2, RoundingMode.HALF_UP);
        districtAggregatedAmt.setValue(districtAmtValue.doubleValue());
        district.setAggregatedAmount(districtAggregatedAmt);

        Area stateAggregatedArea = state.getAggregatedArea();
        BigDecimal stateAreaValue = BigDecimal.valueOf(
                        stateAggregatedArea.getValue() - oldTaskArea + aggregatedArea.get())
                .setScale(2, RoundingMode.HALF_UP);
        stateAggregatedArea.setValue(stateAreaValue.doubleValue());
        state.setAggregatedArea(stateAggregatedArea);

        Amount stateAggregatedAmt = state.getAggregatedAmount();
        BigDecimal stateAmtValue = BigDecimal.valueOf(
                        stateAggregatedAmt.getValue() - oldTaskAmount + aggregatedAmt.get())
                .setScale(2, RoundingMode.HALF_UP);
        stateAggregatedAmt.setValue(stateAmtValue.doubleValue());
        state.setAggregatedAmount(stateAggregatedAmt);

        Area categoryAggregatedArea = category.getAggregatedArea();
        BigDecimal categoryAreaValue = BigDecimal.valueOf(
                        categoryAggregatedArea.getValue() - oldTaskArea + aggregatedArea.get())
                .setScale(2, RoundingMode.HALF_UP);
        categoryAggregatedArea.setValue(categoryAreaValue.doubleValue());
        category.setAggregatedArea(categoryAggregatedArea);

        Amount categoryAggregatedAmt = category.getAggregatedAmount();
        BigDecimal categoryAmtValue = BigDecimal.valueOf(
                        categoryAggregatedAmt.getValue() - oldTaskAmount + aggregatedAmt.get())
                .setScale(2, RoundingMode.HALF_UP);
        categoryAggregatedAmt.setValue(categoryAmtValue.doubleValue());
        category.setAggregatedAmount(categoryAggregatedAmt);

        AmountRequestDto requestedRent = request.rent();
        if (Objects.nonNull(requestedRent)) {
            Amount rent = Optional.ofNullable(task.getRent()).orElse(commonMapper.mapAmount(requestedRent.value()));
            rent.setValue(requestedRent.value());
            if (Objects.isNull(rent.getId())) {
                amountRepository.save(rent);
            }
            task.setRent(rent);
        }

        List<ImageRequestDto> images = request.referenceImages();
        Set<ImageData> referenceImages = Optional.ofNullable(task.getFinalImages()).orElse(new HashSet<>());
        Set<ImageData> newReferenceImages = new HashSet<>();
        Set<Long> storedImageIds = referenceImages.stream().map(ImageData::getId).collect(Collectors.toSet());
        if (Objects.nonNull(images)) {
            images.forEach(
                    imageRequestDto -> {
                        if (storedImageIds.contains(imageRequestDto.id())) {
                            return;
                        }
                        ImageData image = new ImageData();
                        image.setImageData(zip(imageRequestDto.data()));
                        image.setName(imageRequestDto.name());
                        image.setReference(ImageReference.FINAL);
                        referenceImages.add(image);
                        newReferenceImages.add(image);
                    }
            );
            imageRepository.saveAll(newReferenceImages);
        }

        task.setFinalImages(referenceImages);
        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());
    }

    private void saveAllotment(User user, Allotment allotment, AllotmentRequestDto allotmentRequestDto,
                               AtomicReference<Double> aggregatedArea, AtomicReference<Integer> aggregatedQty,
                               AtomicReference<Double> aggregatedAmt, AtomicBoolean isTaskPendingApproval,
                               boolean isRenewRequest, boolean isCreateRequest) {
        Set<ImageData> referenceImages = Optional.ofNullable(allotment.getReferenceImages()).orElse(new HashSet<>());
        Set<ImageData> newReferenceImages = new HashSet<>();
        Set<Long> storedImageIds = referenceImages.stream().map(ImageData::getId).collect(Collectors.toSet());
        allotmentRequestDto.referenceImages().stream()
                .filter(imageRequestDto -> !isRenewRequest || ImageReference.valueOf(imageRequestDto.reference()) == ImageReference.INITIAL)
                .forEach(
                        imageRequestDto -> {
                            if (storedImageIds.contains(imageRequestDto.id())) {
                                return;
                            }
                            ImageData image = new ImageData();
                            image.setImageData(zip(imageRequestDto.data()));
                            image.setName(imageRequestDto.name());
                            if (!isCreateRequest && !isRenewRequest) {
                                image.setReference(ImageReference.valueOf(user.getTeam().getDescription().name()));
                            } else {
                                image.setReference(ImageReference.INITIAL);
                            }
                            referenceImages.add(image);
                            newReferenceImages.add(image);
                        }
                );
        imageRepository.saveAll(newReferenceImages);
        allotment.setReferenceImages(referenceImages);

        Double length = allotmentRequestDto.dimension().length();
        Double width = allotmentRequestDto.dimension().width();
        Dimension dimension = Optional.ofNullable(allotment.getDimension())
                .orElse(commonMapper.mapDimension(length, width));
        if (!dimension.getLength().equals(length) ||
                !dimension.getWidth().equals(width)) {
            dimension.setLength(length);
            dimension.setWidth(width);
        }

        if (Objects.isNull(dimension.getId())) {
            dimensionRepository.save(dimension);
        }
        allotment.setDimension(dimension);


        Integer qtyValue = allotmentRequestDto.quantity().value();
        Quantity qty = Optional.ofNullable(allotment.getQuantity()).orElse(commonMapper.mapQuantity(qtyValue));
        aggregatedQty.updateAndGet(v -> (v + qtyValue));
        if (!qty.getValue().equals(qtyValue)) {
            qty.setValue(qtyValue);
        }
        if (Objects.isNull(qty.getId())) {
            quantityRepository.save(qty);
        }
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
        if (Objects.isNull(currentAssignee.getId())) {
            assigneeRepository.save(currentAssignee);
        }
        allotment.setCurrentAssignee(currentAssignee);

        futureAssignees.remove(currentAssignee);
        assigneeRepository.saveAll(futureAssignees.stream().filter(futureAssignee
                -> Objects.isNull(futureAssignee.getId())).toList());
        allotment.setFutureAssignees(futureAssignees);

        double calculatedArea = calculateArea(dimension, qtyValue);
        aggregatedArea.updateAndGet(v -> BigDecimal.valueOf(v + calculatedArea)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        Area area = Optional.ofNullable(allotment.getArea()).orElse(commonMapper.mapArea(calculatedArea));
        if (area.getValue() != calculatedArea) {
            area.setValue(calculatedArea);
        }
        if (Objects.isNull(area.getId())) {
            areaRepository.save(area);
        }

        allotment.setArea(area);

        Amount unitAmt = productConfig.getAmount();
        double totalAllotmentAmt = (calculatedArea > 0) ? (unitAmt.getValue() * calculatedArea)
                : (unitAmt.getValue() * qtyValue);
        aggregatedAmt.updateAndGet(v -> BigDecimal.valueOf(v + totalAllotmentAmt)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        Amount amount = Optional.ofNullable(allotment.getAmount()).orElse(commonMapper.mapAmount(totalAllotmentAmt));
        if (!amount.getValue().equals(totalAllotmentAmt)) {
            amount.setValue(totalAllotmentAmt);
        }

        if (Objects.isNull(amount.getId())) {
            amountRepository.save(amount);
        }
        allotment.setAmount(amount);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void approve(Long taskId, TaskRequestDto request) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));
        task.setApprovalStatus(ApprovalStatus.APPROVED);
        task.setApprovedAt(LocalDateTime.now());
        Set<Long> requestedAllotmentIds = request.allotments().stream().map(AllotmentRequestDto::id).collect(Collectors.toSet());

        for (Allotment allotment : task.getAllotments()) {
            if (allotment.getApprovalStatus() == ApprovalStatus.PENDING_APPROVAL
                    && requestedAllotmentIds.contains(allotment.getId())) {
                allotment.setApprovalStatus(ApprovalStatus.APPROVED);
                assignAllotmentToNextTeam(allotment, user);
            }
        }

        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void assignToNextTeam(Long taskId, TaskRequestDto request) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));

        Set<Long> requestAllotmentIds =
                request.allotments().stream().map(AllotmentRequestDto::id).collect(Collectors.toSet());
        for (Allotment allotment : task.getAllotments()) {
            if (requestAllotmentIds.contains(allotment.getId())) {
                assignAllotmentToNextTeam(allotment, user);
            }
        }

        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());
    }

    private void assignAllotmentToNextTeam(Allotment allotment, User user) {
        Assignee assignee = allotment.getCurrentAssignee();
        assignee.setStatus(Status.DONE);
        assignee.setEndDate(LocalDateTime.now());
        assignee.setAssignedTo(user);

        Assignee nextAssignee = allotment.getFutureAssignees().stream()
                .min(Comparator.comparingInt(Assignee::getSequence)).orElse(null);
        if (nextAssignee != null) {
            nextAssignee.setStatus(Status.READY_TO_START);
            Set<Assignee> futureAssignees = allotment.getFutureAssignees();
            futureAssignees.remove(nextAssignee);
            allotment.setFutureAssignees(futureAssignees);
        }

        Set<Assignee> earlierAssignees = allotment.getEarlierAssignees();
        earlierAssignees.add(assignee);
        allotment.setEarlierAssignees(earlierAssignees);
        allotment.setCurrentAssignee(nextAssignee);

        allotment.setModifiedBy(user);
        allotment.setLastModifiedAt(LocalDateTime.now());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addImages(Long taskId, TaskRequestDto request) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));

        request.allotments().forEach(
                allotmentRequestDto -> {
                    Allotment allotment = task.getAllotments().stream().filter(element -> element.getId().longValue() == allotmentRequestDto.id())
                            .findFirst().orElseThrow(() -> new RuntimeException(ALLOTMENT_NOT_FOUND));
                    Set<ImageData> referenceImages = Optional.ofNullable(allotment.getReferenceImages()).orElse(new HashSet<>());
                    Set<Long> storedImageIds = referenceImages.stream().map(ImageData::getId).collect(Collectors.toSet());
                    Set<ImageData> newReferenceImages = new HashSet<>();
                    allotmentRequestDto.referenceImages().forEach(
                            imageRequestDto -> {
                                if (storedImageIds.contains(imageRequestDto.id())) {
                                    return;
                                }
                                ImageData image = new ImageData();
                                image.setImageData(zip(imageRequestDto.data()));
                                image.setName(imageRequestDto.name());
                                image.setReference(ImageReference.valueOf(user.getTeam().getDescription().name()));
                                referenceImages.add(image);
                                newReferenceImages.add(image);
                            }
                    );
                    imageRepository.saveAll(newReferenceImages);
                    allotment.setReferenceImages(referenceImages);

                    Assignee assignee = allotment.getCurrentAssignee();
                    assignee.setStatus(Status.IN_PROGRESS);
                    assignee.setStartDate(LocalDateTime.now());
                    assignee.setAssignedTo(user);

                    allotment.setModifiedBy(user);
                    allotment.setLastModifiedAt(LocalDateTime.now());
                }
        );

        List<ImageRequestDto> images = request.referenceImages();
        Set<ImageData> referenceImages = Optional.ofNullable(task.getFinalImages()).orElse(new HashSet<>());
        Set<Long> storedImageIds = referenceImages.stream().map(ImageData::getId).collect(Collectors.toSet());
        Set<ImageData> newReferenceImages = new HashSet<>();
        if (Objects.nonNull(images)) {
            images.forEach(
                    imageRequestDto -> {
                        if (storedImageIds.contains(imageRequestDto.id())) {
                            return;
                        }
                        ImageData image = new ImageData();
                        image.setImageData(zip(imageRequestDto.data()));
                        image.setName(imageRequestDto.name());
                        image.setReference(ImageReference.FINAL);
                        referenceImages.add(image);
                        newReferenceImages.add(image);
                    }
            );
            imageRepository.saveAll(newReferenceImages);
        }
        task.setFinalImages(referenceImages);
        task.setLastModifiedBy(user);
        task.setLastModifiedAt(LocalDateTime.now());
    }

    public void renew(TaskRequestDto request, Long originalTaskId) {
        addTask(request, true, originalTaskId);
    }

}
