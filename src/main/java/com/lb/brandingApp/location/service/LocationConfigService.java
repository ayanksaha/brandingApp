package com.lb.brandingApp.location.service;

import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.category.data.entities.State;
import com.lb.brandingApp.category.data.models.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.category.repository.CategoryRepository;
import com.lb.brandingApp.category.repository.StateRepository;
import com.lb.brandingApp.location.data.entities.DistrictConfig;
import com.lb.brandingApp.location.data.entities.StateConfig;
import com.lb.brandingApp.location.data.models.request.DistrictConfigRequestDto;
import com.lb.brandingApp.location.data.models.request.StateConfigRequestDto;
import com.lb.brandingApp.location.data.models.response.DistrictConfigResponseDto;
import com.lb.brandingApp.location.data.models.response.StateConfigResponseDto;
import com.lb.brandingApp.location.repository.StateConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;

//Dropdown values will not be paginated.
@Service
@Transactional
public class LocationConfigService {

    @Autowired
    private StateConfigRepository stateConfigRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StateRepository stateRepository;

    public List<StateConfigResponseDto> getAllStates(Long categoryId) {
        List<StateConfig> result = stateConfigRepository.findAll();
        if (Objects.nonNull(categoryId)) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
            List<StateConfig> stateConfigs = category.getStates()
                    .stream().map(State::getStateConfig).toList();
            result = result.stream().filter(stateConfig ->
                    !stateConfigs.stream().map(StateConfig::getId).toList().contains(stateConfig.getId())).toList();
        }
        return result.stream().map(
                        stateConfig -> StateConfigResponseDto.builder()
                                .stateId(stateConfig.getId())
                                .stateName(stateConfig.getName())
                                .build())
                .sorted(Comparator.comparing(StateConfigResponseDto::getStateName)).toList();
    }

    public List<DistrictConfigResponseDto> getAllDistrictsByState(Long categoryId, Long stateConfigId, Long stateId) {
        StateConfig stateConfig = stateConfigRepository.findById(stateConfigId)
                .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
        Set<DistrictConfig> result = stateConfig.getDistricts();
        if (Objects.nonNull(categoryId) && Objects.nonNull(stateId)) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
            State state = stateRepository.findByIdAndCategory(stateId, category)
                    .orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
            List<DistrictConfig> districtConfigs = state.getDistricts()
                    .stream().map(District::getDistrictConfig).toList();
            result = result.stream().filter(districtConfig ->
                            !districtConfigs.stream().map(DistrictConfig::getId).toList().contains(districtConfig.getId()))
                    .collect(Collectors.toSet());
        }
        return result.stream().map(
                districtConfig -> DistrictConfigResponseDto.builder()
                        .districtId(districtConfig.getId())
                        .districtName(districtConfig.getName())
                        .stateId(stateConfigId)
                        .build()
        ).sorted(Comparator.comparing(DistrictConfigResponseDto::getDistrictName)).toList();
    }
}
