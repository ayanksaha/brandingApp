package com.lb.brandingApp.category.service;

import com.lb.brandingApp.auth.repository.TeamRepository;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.category.data.models.request.CategoryRequestDto;
import com.lb.brandingApp.category.data.models.request.DistrictRequestDto;
import com.lb.brandingApp.category.data.models.request.StateRequestDto;
import com.lb.brandingApp.category.data.models.response.*;
import com.lb.brandingApp.category.data.projections.CategorySummary;
import com.lb.brandingApp.category.repository.CategoryRepository;
import com.lb.brandingApp.category.repository.DistrictRepository;
import com.lb.brandingApp.category.repository.StateRepository;
import com.lb.brandingApp.common.data.entities.*;
import com.lb.brandingApp.common.data.enums.ImageReference;
import com.lb.brandingApp.common.data.models.request.ImageRequestDto;
import com.lb.brandingApp.common.data.models.request.TimePeriodRequestDto;
import com.lb.brandingApp.common.data.models.response.*;
import com.lb.brandingApp.common.mapper.CommonMapper;
import com.lb.brandingApp.common.repository.*;
import com.lb.brandingApp.location.data.entities.DistrictConfig;
import com.lb.brandingApp.location.data.entities.StateConfig;
import com.lb.brandingApp.location.repository.DistrictConfigRepository;
import com.lb.brandingApp.location.repository.StateConfigRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;
import static com.lb.brandingApp.app.utils.CompressionUtil.unzip;
import static com.lb.brandingApp.app.utils.CompressionUtil.zip;

@Slf4j
@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TimePeriodRepository timePeriodRepository;

    @Autowired
    private WorkflowItemRepository workflowItemRepository;

    @Autowired
    private StateConfigRepository stateConfigRepository;

    @Autowired
    private DistrictConfigRepository districtConfigRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private QuantityRepository quantityRepository;

    @Autowired
    private AmountRepository amountRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CommonMapper commonMapper;

    @Value("${categories.default.sort.by}")
    private String defaultSortBy;

    @Value("${categories.default.sort.order}")
    private String defaultSortOrder;

    @Value("${default.page.size}")
    private Integer defaultPageSize;

    /*
        Collection<AppStatusResource> data = new ArrayList<>();
        data.add(appStatusResource1);
        data.add(appStatusResource2);
        data.add(appStatusResource3);
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(data.size(), 1, data.size(), 1);
        PagedModel<AppStatusResource> result = new PagedModel<>(data, metadata);
     */
    public PageResponseDto<CategoryResponseDto> getAllCategories(
            Integer pageNumber, Integer pageSize, String sortOrder, String sortBy, String name) {
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        if (StringUtils.isNotBlank(name)) {
            return getPaginatedCategoriesResponseByName(name, page);
        }
        return getPaginatedCategoriesResponse(page);
    }

    private PageResponseDto<CategoryResponseDto> getPaginatedCategoriesResponse(Pageable page) {
        Page<Category> result = categoryRepository.findAll(page);
        List<CategoryResponseDto> response = result.stream().map(category -> CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(Objects.nonNull(category.getIcon()) ?
                        ImageResponseDto.builder()
                                .image(unzip(category.getIcon().getImageData()))
                                .name(category.getIcon().getName())
                                .build()
                        : null)
                .aggregatedAmount(AmountResponseDto.builder()
                        .value(category.getAggregatedAmount().getValue())
                        .currency(category.getAggregatedAmount().getCurrency())
                        .build())
                .aggregatedQuantity(QuantityResponseDto.builder()
                        .value(category.getAggregatedQuantity().getValue())
                        .unit(category.getAggregatedQuantity().getUom())
                        .build())
                .aggregatedArea(AreaResponseDto.builder()
                        .value(category.getAggregatedArea().getValue())
                        .unit(category.getAggregatedArea().getUnit())
                        .build())
                .build()
        ).toList();
        return PageResponseDto.<CategoryResponseDto>builder()
                .content(response)
                .metadata(
                        PageResponseDto.PagingMetadata
                                .builder()
                                .pageSize(result.getNumberOfElements())
                                .pageNumber(result.getNumber())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .build())
                .build();
    }

    private PageResponseDto<CategoryResponseDto> getPaginatedCategoriesResponseByName(String name, Pageable page) {
        Page<CategorySummary> result = categoryRepository.findAllByNameContaining(name, page);
        List<CategoryResponseDto> response = result.stream().map(category -> CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(Objects.nonNull(category.getIcon()) ?
                        ImageResponseDto.builder()
                                .image(unzip(category.getIcon().getImageData()))
                                .build()
                        : null)
                .build()
        ).toList();

        return PageResponseDto.<CategoryResponseDto>builder()
                .content(response)
                .metadata(
                        PageResponseDto.PagingMetadata
                                .builder()
                                .pageSize(result.getNumberOfElements())
                                .pageNumber(result.getNumber())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .build())
                .build();
    }

    public CategoryResponseDto getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(Objects.nonNull(category.getIcon()) ?
                        ImageResponseDto.builder()
                                .image(unzip(category.getIcon().getImageData()))
                                .name(category.getIcon().getName())
                                .build()
                        : null)
                .aggregatedAmount(AmountResponseDto.builder()
                        .value(category.getAggregatedAmount().getValue())
                        .currency(category.getAggregatedAmount().getCurrency())
                        .build())
                .aggregatedQuantity(QuantityResponseDto.builder()
                        .value(category.getAggregatedQuantity().getValue())
                        .unit(category.getAggregatedQuantity().getUom())
                        .build())
                .aggregatedArea(AreaResponseDto.builder()
                        .value(category.getAggregatedArea().getValue())
                        .unit(category.getAggregatedArea().getUnit())
                        .build())
                .build();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addNewCategory(CategoryRequestDto request) {

        Category category = new Category();
        category.setName(request.name());

        ImageRequestDto icon = request.icon();
        if (Objects.nonNull(icon)) {
            ImageData image = new ImageData();
            image.setImageData(zip(icon.data()));
            image.setName(icon.name());
            image.setReference(ImageReference.ICON);
            imageRepository.save(image);
            category.setIcon(image);
        }

        TimePeriodRequestDto verificationInterval = request.verificationInterval();
        if(Objects.nonNull(verificationInterval)) {
            TimePeriod interval = new TimePeriod();
            interval.setUnit(verificationInterval.unit());
            interval.setValue(verificationInterval.value());
            timePeriodRepository.save(interval);
            category.setVerificationInterval(interval);
        }

        Amount aggregatedAmount = commonMapper.mapAmount(0.0);
        amountRepository.save(aggregatedAmount);
        category.setAggregatedAmount(aggregatedAmount);

        Quantity aggregatedQty = commonMapper.mapQuantity(0);
        quantityRepository.save(aggregatedQty);
        category.setAggregatedQuantity(aggregatedQty);

        Area aggregatedArea = commonMapper.mapArea(0.0);
        areaRepository.save(aggregatedArea);
        category.setAggregatedArea(aggregatedArea);
        categoryRepository.save(category);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateCategory(Long categoryId, CategoryRequestDto request) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));
        String categoryName = request.name();
        if (Objects.nonNull(categoryName)) {
            category.setName(categoryName);
        }
        ImageRequestDto categoryIcon = request.icon();
        if (Objects.nonNull(categoryIcon)) {
            ImageData image = category.getIcon();
            image.setImageData(zip(categoryIcon.data()));
            image.setName(categoryIcon.name());
            imageRepository.save(image);
            category.setIcon(image);
        }
        categoryRepository.save(category);
    }

    public PageResponseDto<StateResponseDto> getStatesByCategory(Long categoryId,
                                                                 Integer pageNumber, Integer pageSize, String sortOrder, String sortBy) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<State> result = stateRepository.findAllByCategory(category, page);
        List<StateResponseDto> response = result.stream().map(
                state -> StateResponseDto.builder()
                        .stateId(state.getId())
                        .stateConfigId(state.getStateConfig().getId())
                        .name(state.getStateConfig().getName())
                        .categoryId(state.getCategory().getId())
                        .aggregatedAmount(AmountResponseDto.builder()
                                .value(state.getAggregatedAmount().getValue())
                                .currency(state.getAggregatedAmount().getCurrency())
                                .build())
                        .aggregatedQuantity(QuantityResponseDto.builder()
                                .value(state.getAggregatedQuantity().getValue())
                                .unit(state.getAggregatedQuantity().getUom())
                                .build())
                        .aggregatedArea(AreaResponseDto.builder()
                                .value(state.getAggregatedArea().getValue())
                                .unit(state.getAggregatedArea().getUnit())
                                .build())
                        .build()).toList();
        return PageResponseDto.<StateResponseDto>builder()
                .content(response)
                .metadata(
                        PageResponseDto.PagingMetadata
                                .builder()
                                .pageSize(result.getNumberOfElements())
                                .pageNumber(result.getNumber())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .build())
                .build();
    }

    public StateResponseDto getState(Long categoryId, Long stateId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));
        State state = stateRepository.findByIdAndCategory(stateId, category).orElseThrow(
                () -> new RuntimeException(STATE_NOT_FOUND));

        return StateResponseDto.builder()
                .stateId(state.getId())
                .stateConfigId(state.getStateConfig().getId())
                .name(state.getStateConfig().getName())
                .categoryId(state.getCategory().getId())
                .aggregatedAmount(AmountResponseDto.builder()
                        .value(state.getAggregatedAmount().getValue())
                        .currency(state.getAggregatedAmount().getCurrency())
                        .build())
                .aggregatedQuantity(QuantityResponseDto.builder()
                        .value(state.getAggregatedQuantity().getValue())
                        .unit(state.getAggregatedQuantity().getUom())
                        .build())
                .aggregatedArea(AreaResponseDto.builder()
                        .value(state.getAggregatedArea().getValue())
                        .unit(state.getAggregatedArea().getUnit())
                        .build())
                .build();
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void addStateToCategory(StateRequestDto request, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));

        StateConfig stateConfig = stateConfigRepository.findById(request.stateConfigId())
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));

        if (stateRepository.findByStateConfigAndCategory(stateConfig, category).isPresent()) {
            throw new RuntimeException(STATE_ALREADY_CONFIGURED);
        }

        State state = new State();
        state.setStateConfig(stateConfig);
        state.setCategory(category);

        Amount aggregatedAmount = commonMapper.mapAmount(0.0);
        amountRepository.save(aggregatedAmount);
        state.setAggregatedAmount(aggregatedAmount);

        Quantity aggregatedQty = commonMapper.mapQuantity(0);
        quantityRepository.save(aggregatedQty);
        state.setAggregatedQuantity(aggregatedQty);

        Area aggregatedArea = commonMapper.mapArea(0.0);
        areaRepository.save(aggregatedArea);
        state.setAggregatedArea(aggregatedArea);

        stateRepository.save(state);
    }

    public PageResponseDto<DistrictResponseDto> getAllDistrictsByState(Long categoryId, Long stateId,
                                                                       Integer pageNumber, Integer pageSize, String sortOrder, String sortBy) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        State state = stateRepository.findByIdAndCategory(stateId, category)
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
        Pageable page = PageRequest.of(
                Optional.ofNullable(pageNumber).orElse(0),
                Optional.ofNullable(pageSize).orElse(defaultPageSize),
                Sort.by(Sort.Direction.valueOf(Optional.ofNullable(sortOrder).orElse(defaultSortOrder)),
                        Optional.ofNullable(sortBy).orElse(defaultSortBy)));
        Page<District> result = districtRepository.findAllByState(state, page);
        List<DistrictResponseDto> response = result.stream().map(
                district -> DistrictResponseDto.builder()
                        .districtId(district.getId())
                        .districtConfigId(district.getDistrictConfig().getId())
                        .name(district.getDistrictConfig().getName())
                        .stateId(district.getState().getId())
                        .categoryId(district.getState().getCategory().getId())
                        .aggregatedAmount(AmountResponseDto.builder()
                                .value(district.getAggregatedAmount().getValue())
                                .currency(district.getAggregatedAmount().getCurrency())
                                .build())
                        .aggregatedQuantity(QuantityResponseDto.builder()
                                .value(district.getAggregatedQuantity().getValue())
                                .unit(district.getAggregatedQuantity().getUom())
                                .build())
                        .aggregatedArea(AreaResponseDto.builder()
                                .value(district.getAggregatedArea().getValue())
                                .unit(district.getAggregatedArea().getUnit())
                                .build())
                        .build()).toList();
        return PageResponseDto.<DistrictResponseDto>builder()
                .content(response)
                .metadata(
                        PageResponseDto.PagingMetadata
                                .builder()
                                .pageSize(result.getNumberOfElements())
                                .pageNumber(result.getNumber())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .build())
                .build();
    }

    public DistrictResponseDto getDistrict(Long categoryId, Long stateId, Long districtId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));
        State state = stateRepository.findByIdAndCategory(stateId, category).orElseThrow(
                () -> new RuntimeException(STATE_NOT_FOUND));
        District district = districtRepository.findByIdAndState(districtId, state)
                .orElseThrow(() -> new RuntimeException(DISTRICT_NOT_FOUND));

        return DistrictResponseDto.builder()
                .districtId(district.getId())
                .districtConfigId(district.getDistrictConfig().getId())
                .name(district.getDistrictConfig().getName())
                .stateId(district.getState().getId())
                .categoryId(district.getState().getCategory().getId())
                .aggregatedAmount(AmountResponseDto.builder()
                        .value(district.getAggregatedAmount().getValue())
                        .currency(district.getAggregatedAmount().getCurrency())
                        .build())
                .aggregatedQuantity(QuantityResponseDto.builder()
                        .value(district.getAggregatedQuantity().getValue())
                        .unit(district.getAggregatedQuantity().getUom())
                        .build())
                .aggregatedArea(AreaResponseDto.builder()
                        .value(district.getAggregatedArea().getValue())
                        .unit(district.getAggregatedArea().getUnit())
                        .build())
                .build();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addDistrictToCategory(DistrictRequestDto request, Long categoryId, Long stateId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));

        DistrictConfig districtConfig = districtConfigRepository.findById(request.districtConfigId())
                .orElseThrow(() -> new RuntimeException(DISTRICT_NOT_FOUND));

        State state = category.getStates().stream().filter(
                        stateInDb -> stateInDb.getId().longValue() == stateId.longValue()).findAny()
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));

        if (districtRepository.findByDistrictConfigAndState(districtConfig, state).isPresent()) {
            throw new RuntimeException(DISTRICT_ALREADY_CONFIGURED);
        }

        District district = new District();
        district.setState(state);
        district.setDistrictConfig(districtConfig);

        Amount aggregatedAmount = commonMapper.mapAmount(0.0);
        amountRepository.save(aggregatedAmount);
        district.setAggregatedAmount(aggregatedAmount);

        Quantity aggregatedQty = commonMapper.mapQuantity(0);
        quantityRepository.save(aggregatedQty);
        district.setAggregatedQuantity(aggregatedQty);

        Area aggregatedArea = commonMapper.mapArea(0.0);
        areaRepository.save(aggregatedArea);
        district.setAggregatedArea(aggregatedArea);

        districtRepository.save(district);
    }
}
