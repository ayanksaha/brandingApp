package com.lb.brandingApp.task.service;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.auth.data.models.common.UserExtension;
import com.lb.brandingApp.auth.data.models.response.UserResponseDto;
import com.lb.brandingApp.auth.repository.UserRepository;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.category.data.models.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.models.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.category.data.models.response.StateResponseDto;
import com.lb.brandingApp.category.repository.DistrictRepository;
import com.lb.brandingApp.common.data.entities.ImageData;
import com.lb.brandingApp.common.data.enums.ImageReference;
import com.lb.brandingApp.common.data.enums.Status;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.data.models.response.ImageResponseDto;
import com.lb.brandingApp.common.repository.ImageRepository;
import com.lb.brandingApp.task.data.entities.AdhocTask;
import com.lb.brandingApp.task.data.entities.Task;
import com.lb.brandingApp.task.data.models.request.AdhocTaskRequestDto;
import com.lb.brandingApp.task.data.models.response.AdhocTaskResponseDto;
import com.lb.brandingApp.task.mapper.TaskMapper;
import com.lb.brandingApp.task.repository.AdhocTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;
import static com.lb.brandingApp.app.utils.CompressionUtil.unzip;
import static com.lb.brandingApp.app.utils.CompressionUtil.zip;

@Service
public class AdhocTaskService {

    @Autowired
    private AdhocTaskRepository adhocTaskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Value("${tasks.default.sort.by}")
    private String defaultSortBy;
    @Value("${tasks.default.sort.order}")
    private String defaultSortOrder;
    @Value("${default.page.size}")
    private Integer defaultPageSize;
    @Value("${task.status.warning.period.days}")
    private int warningPeriod;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public AdhocTaskResponseDto createAdhocTask(AdhocTaskRequestDto request) {
        UserExtension userExtension = (UserExtension) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        District district = districtRepository.findById(request.district().districtId())
                .orElseThrow(() -> new RuntimeException(DISTRICT_NOT_FOUND));
        AdhocTask adhocTask = mapAdhocTaskEntity(request, district);
        adhocTask.setCreatedBy(user);
        adhocTask.setCreatedAt(LocalDateTime.now());
        adhocTask.setLastModifiedBy(user);
        adhocTask.setLastModifiedAt(LocalDateTime.now());
        AdhocTask saveAdhocTask = adhocTaskRepository.save(adhocTask);
        return mapTaskDetailResponse(saveAdhocTask);
    }

    public PageResponseDto<AdhocTaskResponseDto> getAllAdhocTasksByUser(
            Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        final UserExtension userExtension =
                (UserExtension) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userExtension.getUsername())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<AdhocTask> result = adhocTaskRepository.findAllByCreatedBy(user, page);
        List<AdhocTaskResponseDto> response = result.stream().map(this::mapTaskListResponse).toList();
        return PageResponseDto.<AdhocTaskResponseDto>builder()
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

    public PageResponseDto<AdhocTaskResponseDto> getAllAdhocTasks(
            Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<AdhocTask> result = adhocTaskRepository.findAllByConvertedTask(null, page);
        List<AdhocTaskResponseDto> response = result.stream().map(this::mapTaskListResponse).toList();
        return PageResponseDto.<AdhocTaskResponseDto>builder()
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

    public AdhocTaskResponseDto getAdhocTaskById(Long adhocTaskId) {
        AdhocTask adhocTask = adhocTaskRepository.findById(adhocTaskId)
                .orElseThrow(() -> new RuntimeException(TASK_NOT_FOUND));
        return mapTaskDetailResponse(adhocTask);
    }

    private AdhocTaskResponseDto mapTaskListResponse(AdhocTask task) {
        boolean isTaskConverted = false;
        Status convertedTaskStatus = Status.IN_PROGRESS;

        if (Objects.nonNull(task.getConvertedTask())) {
            isTaskConverted = true;
            Task convertedTask = task.getConvertedTask();
            convertedTaskStatus = taskMapper.mapTaskStatus(convertedTask);
        }

        District district = task.getDistrict();
        State state = district.getState();
        Category category = state.getCategory();

        return AdhocTaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .isConverted(isTaskConverted)
                .taskStatus(mapTaskStatus(convertedTaskStatus, task))
                .isConverted(task.getConvertedTask() != null)
                .district(DistrictResponseDto.builder()
                        .districtId(district.getId())
                        .name(district.getDistrictConfig().getName())
                        .build())
                .state(StateResponseDto.builder()
                        .stateId(state.getId())
                        .name(state.getStateConfig().getName())
                        .build())
                .category(CategoryResponseDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .build();
    }

    private AdhocTaskResponseDto mapTaskDetailResponse(AdhocTask task) {
        boolean isTaskConverted = false;
        Status convertedTaskStatus = Status.IN_PROGRESS;
        Long convertedTaskId = null;

        if (Objects.nonNull(task.getConvertedTask())) {
            isTaskConverted = true;
            Task convertedTask = task.getConvertedTask();
            convertedTaskId = convertedTask.getId();
            convertedTaskStatus = taskMapper.mapTaskStatus(convertedTask);
        }

        District district = task.getDistrict();
        State state = district.getState();
        Category category = state.getCategory();

        Set<ImageResponseDto> imageResponses = new HashSet<>();
        if(Objects.nonNull(task.getReferenceImages()) && !task.getReferenceImages().isEmpty()) {
            for(ImageData refImage: task.getReferenceImages()) {
                ImageResponseDto imageResponse = ImageResponseDto.builder()
                        .image(unzip(refImage.getImageData()))
                        .name(refImage.getName())
                        .reference(refImage.getReference().name())
                        .build();
                imageResponses.add(imageResponse);
            }
        }

        return AdhocTaskResponseDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .location(task.getLocation())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .isConverted(isTaskConverted)
                .isConverted(task.getConvertedTask() != null)
                .convertedTaskId(convertedTaskId)
                .convertedTaskStatus(convertedTaskStatus)
                .convertedTaskStatusDescription(convertedTaskStatus.toString())
                .createdAt(task.getCreatedAt())
                .createdBy(mapUserResponse(task.getCreatedBy()))
                .lastModifiedAt(task.getLastModifiedAt())
                .lastModifiedBy(mapUserResponse(task.getLastModifiedBy()))
                .district(DistrictResponseDto.builder()
                        .districtId(district.getId())
                        .name(district.getDistrictConfig().getName())
                        .build())
                .state(StateResponseDto.builder()
                        .stateId(state.getId())
                        .name(state.getStateConfig().getName())
                        .build())
                .category(CategoryResponseDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .referenceImages(imageResponses)
                .build();
    }

    private Status mapTaskStatus(Status convertedTaskStatus, AdhocTask adhocTask) {
        if ((adhocTask.getConvertedTask() == null) &&
                LocalDateTime.now().minus(warningPeriod, ChronoUnit.DAYS).isAfter(adhocTask.getCreatedAt())) {
            return Status.PENDING;
        }
        if (((convertedTaskStatus == Status.IN_PROGRESS) || (convertedTaskStatus == Status.PENDING_APPROVAL)
                || (convertedTaskStatus == Status.READY_TO_START))
                && adhocTask.getCreatedAt().plus(warningPeriod, ChronoUnit.DAYS).isBefore(LocalDateTime.now())) {
            return Status.PENDING;
        }
        return convertedTaskStatus;
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

    private AdhocTask mapAdhocTaskEntity(AdhocTaskRequestDto request, District district) {
        AdhocTask adhocTask = new AdhocTask();
        adhocTask.setName(request.name());
        adhocTask.setDescription(request.description());
        adhocTask.setLocation(request.location());
        adhocTask.setLatitude(request.latitude());
        adhocTask.setLongitude(request.longitude());
        adhocTask.setDistrict(district);

        Set<ImageData> referenceImages = new HashSet<>();
        if (Objects.nonNull(request.referenceImages()) && !request.referenceImages().isEmpty()) {
            for (ImageRequestDto imageRequest : request.referenceImages()) {
                ImageData image = new ImageData();
                image.setImageData(zip(imageRequest.data()));
                image.setReference(ImageReference.INITIAL);
                image.setName(imageRequest.name());
                referenceImages.add(image);
            }
            imageRepository.saveAll(referenceImages);
        }
        adhocTask.setReferenceImages(referenceImages);
        return adhocTask;
    }
}
