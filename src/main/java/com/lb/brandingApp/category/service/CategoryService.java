package com.lb.brandingApp.category.service;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.category.data.models.request.*;
import com.lb.brandingApp.category.data.models.response.*;
import com.lb.brandingApp.common.data.entities.*;
import com.lb.brandingApp.common.data.models.request.TimePeriodRequestDto;
import com.lb.brandingApp.common.data.models.response.AmountResponseDto;
import com.lb.brandingApp.common.data.models.response.AreaResponseDto;
import com.lb.brandingApp.common.data.models.response.QuantityResponseDto;
import com.lb.brandingApp.common.data.models.response.TimePeriodResponseDto;
import com.lb.brandingApp.common.repository.*;
import com.lb.brandingApp.config.data.entities.DistrictConfig;
import com.lb.brandingApp.config.data.entities.StateConfig;
import com.lb.brandingApp.auth.repository.TeamRepository;
import com.lb.brandingApp.category.repository.CategoryRepository;
import com.lb.brandingApp.category.repository.DistrictRepository;
import com.lb.brandingApp.category.repository.StateRepository;
import com.lb.brandingApp.config.repository.DistrictConfigRepository;
import com.lb.brandingApp.config.repository.StateConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;
import static com.lb.brandingApp.app.utils.AppUtil.*;

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

    public List<CategoryResponseDto> getAllCategories() {
        List<CategoryResponseDto> response = categoryRepository.findAll().stream().map(category -> CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .validity(TimePeriodResponseDto.builder()
                        .id(category.getValidity().getId())
                        .value(category.getValidity().getValue())
                        .unit(category.getValidity().getUnit())
                        .build())
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
                .workflow(getWorkflow(category))
                .build()
        ).toList();
        return response.stream().sorted(Comparator.comparingDouble(
                responseDto -> ((CategoryResponseDto)responseDto).getAggregatedAmount().getValue()).reversed()).toList();
    }

    LinkedHashSet<WorkflowItemResponseDto> getWorkflow(Category category) {
        LinkedHashSet<WorkflowItemResponseDto> workflowInResponse = new LinkedHashSet<>();
        List<WorkflowItem> workflowInDb = new ArrayList<>(category.getWorkflow());
        workflowInDb.sort(Comparator.comparing(WorkflowItem::getItemNumber));
        for (WorkflowItem item : workflowInDb) {
            Team team = item.getTeam();
            workflowInResponse.add(WorkflowItemResponseDto.builder()
                    .teamId(team.getId())
                    .name(team.getDescription().name())
                    .description(team.getDescription().description())
                    .order(item.getItemNumber())
                    .build());
        }
        return workflowInResponse;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addNewCategory(CategoryRequestDto request) {

        Category category = new Category();
        category.setName(request.name());

        TimePeriodRequestDto validityRequest = request.validity();
        TimePeriod validity = new TimePeriod();
        validity.setUnit(validityRequest.unit());
        validity.setValue(validityRequest.value());
        category.setValidity(validity);

        Amount aggregatedAmount = mapAmount(0.0);
        amountRepository.save(aggregatedAmount);
        category.setAggregatedAmount(aggregatedAmount);

        Quantity aggregatedQty = mapQuantity(0);
        quantityRepository.save(aggregatedQty);
        category.setAggregatedQuantity(aggregatedQty);

        Area aggregatedArea = mapArea(0.0);
        areaRepository.save(aggregatedArea);
        category.setAggregatedArea(aggregatedArea);

        Set<WorkflowItem> workflow = new LinkedHashSet<>();

        for (WorkflowItemRequestDto workflowItem : request.workflow()) {
            Team team = teamRepository.findById(workflowItem.id())
                    .orElseThrow(() -> {
                        categoryRepository.delete(category);
                        return new RuntimeException(TEAM_NOT_FOUND);
                    });
            Optional<WorkflowItem> workflowItemInDb = workflowItemRepository
                    .findByCategoryAndTaskAndTeam(null, null, team);
            WorkflowItem item;
            if (workflowItemInDb.isEmpty()) {
                item = new WorkflowItem();
                item.setTeam(team);
                item.setItemNumber(workflowItem.order());
                workflowItemRepository.save(item);
            } else {
                item = workflowItemInDb.get();
            }
            workflow.add(item);
        }
        category.setWorkflow(workflow);
        categoryRepository.save(category);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateCategory(Long categoryId, CategoryRequestDto request) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));

        category.setName(request.name());
        TimePeriodRequestDto validityRequest = request.validity();

        if (Objects.nonNull(validityRequest)) {
            if (Objects.isNull(validityRequest.id())) {
                throw new RuntimeException(VALIDITY_NOT_FOUND);
            }

            TimePeriod validity = timePeriodRepository.findById(validityRequest.id()).orElseThrow(() -> new RuntimeException(VALIDITY_NOT_FOUND));
            validity.setValue(validityRequest.value());
            validity.setUnit(validityRequest.unit());
            timePeriodRepository.save(validity);

            category.setValidity(validity);
        }

        Set<WorkflowItem> workflow = new LinkedHashSet<>();

        //Setting workflow for newer tasks only
        for (WorkflowItemRequestDto workflowItem : request.workflow()) {
            Team team = teamRepository.findById(workflowItem.id())
                    .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
            Optional<WorkflowItem> workflowItemInDb = workflowItemRepository.findByCategoryAndTaskAndTeam(category, null, team);
            WorkflowItem item;
            if (workflowItemInDb.isEmpty()) {
                workflowItemInDb = workflowItemRepository.findByCategoryAndTaskAndTeam(null, null, team);
            }
            if (workflowItemInDb.isEmpty()) {
                item = new WorkflowItem();
                item.setTeam(team);
                item.setItemNumber(workflowItem.order());
                workflowItemRepository.save(item);
            } else {
                item = workflowItemInDb.get();
            }
            workflow.add(item);
        }
        category.setWorkflow(workflow);
        categoryRepository.save(category);
    }

    public List<StateResponseDto> getStatesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(CATEGORY_NOT_FOUND));
        List<StateResponseDto> response = category.getStates().stream().map(
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
        return response.stream().sorted(Comparator.comparingDouble(
                responseDto -> ((StateResponseDto) responseDto).getAggregatedAmount().getValue()).reversed()).toList();
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void addStateToCategory(StateRequestDto request, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));

        StateConfig stateConfig = stateConfigRepository.findById(request.stateId())
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));

        if (stateRepository.findByStateConfigAndCategory(stateConfig, category).isPresent()) {
            throw new RuntimeException(STATE_ALREADY_CONFIGURED);
        }

        State state = new State();
        state.setStateConfig(stateConfig);
        state.setCategory(category);

        Amount aggregatedAmount = mapAmount(0.0);
        amountRepository.save(aggregatedAmount);
        state.setAggregatedAmount(aggregatedAmount);

        Quantity aggregatedQty = mapQuantity(0);
        quantityRepository.save(aggregatedQty);
        state.setAggregatedQuantity(aggregatedQty);

        Area aggregatedArea = mapArea(0.0);
        areaRepository.save(aggregatedArea);
        state.setAggregatedArea(aggregatedArea);

        stateRepository.save(state);
    }

    public List<DistrictResponseDto> getAllDistrictsByState(Long categoryId, Long stateId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        Set<State> states = category.getStates();

        for (State state : states) {
            if (stateId.longValue() == state.getId().longValue()) {
                return state.getDistricts().stream().map(
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
                                .build()
                ).sorted(Comparator.comparingDouble(district -> ((DistrictResponseDto) district).getAggregatedAmount().getValue()).reversed()).toList();
            }
        }
        return new ArrayList<>();
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

        Amount aggregatedAmount = mapAmount(0.0);
        amountRepository.save(aggregatedAmount);
        district.setAggregatedAmount(aggregatedAmount);

        Quantity aggregatedQty = mapQuantity(0);
        quantityRepository.save(aggregatedQty);
        district.setAggregatedQuantity(aggregatedQty);

        Area aggregatedArea = mapArea(0.0);
        areaRepository.save(aggregatedArea);
        district.setAggregatedArea(aggregatedArea);

        districtRepository.save(district);
    }
}
