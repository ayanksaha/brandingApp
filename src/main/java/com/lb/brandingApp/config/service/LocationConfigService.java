package com.lb.brandingApp.config.service;

import com.lb.brandingApp.config.data.entities.DistrictConfig;
import com.lb.brandingApp.config.data.entities.StateConfig;
import com.lb.brandingApp.config.data.models.request.DistrictConfigRequestDto;
import com.lb.brandingApp.config.data.models.request.StateConfigRequestDto;
import com.lb.brandingApp.config.data.models.response.DistrictConfigResponseDto;
import com.lb.brandingApp.config.data.models.response.StateConfigResponseDto;
import com.lb.brandingApp.config.repository.StateConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.lb.brandingApp.app.constants.ApplicationConstants.STATE_ALREADY_CONFIGURED;
import static com.lb.brandingApp.app.constants.ApplicationConstants.STATE_NOT_FOUND;

@Service
@Transactional
public class LocationConfigService {

    @Autowired
    private StateConfigRepository stateConfigRepository;

    public List<StateConfigResponseDto> getAllStates() {
        return stateConfigRepository.findAll().stream().map(
                stateConfig -> StateConfigResponseDto.builder()
                        .stateId(stateConfig.getId())
                        .stateName(stateConfig.getName())
                        .build()
        ).sorted(Comparator.comparing(StateConfigResponseDto::getStateName)).toList();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addState(StateConfigRequestDto request) {
        if(stateConfigRepository.findByName(request.stateName()).isPresent()) {
            throw new RuntimeException(STATE_ALREADY_CONFIGURED);
        }

        StateConfig state = new StateConfig();
        state.setName(request.stateName());
        stateConfigRepository.save(state);
    }

    public List<DistrictConfigResponseDto> getAllDistrictsByState(Long stateId) {
        StateConfig state = stateConfigRepository.findById(stateId).orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
        return state.getDistricts().stream().map(
                districtConfig -> DistrictConfigResponseDto.builder()
                        .districtId(districtConfig.getId())
                        .districtName(districtConfig.getName())
                        .stateId(stateId)
                        .build()
        ).sorted(Comparator.comparing(DistrictConfigResponseDto::getDistrictName)).toList();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addDistrictToState(Long stateId, DistrictConfigRequestDto request) {
        StateConfig state = stateConfigRepository.findById(stateId).orElseThrow(() -> new RuntimeException(STATE_NOT_FOUND));
        Set<DistrictConfig> districts = state.getDistricts();

        DistrictConfig district = new DistrictConfig();
        district.setName(request.districtName());
        districts.add(district);
        stateConfigRepository.save(state);
    }
}
