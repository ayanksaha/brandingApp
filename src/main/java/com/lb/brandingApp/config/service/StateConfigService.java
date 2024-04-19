package com.lb.brandingApp.config.service;

import com.lb.brandingApp.config.data.dto.request.DistrictConfigRequestDto;
import com.lb.brandingApp.config.data.dto.response.DistrictConfigResponseDto;
import com.lb.brandingApp.config.data.dto.request.StateConfigRequestDto;
import com.lb.brandingApp.config.data.dto.response.StateConfigResponseDto;

import java.util.List;

public interface StateConfigService {

    List<StateConfigResponseDto> getAllStates();

    void addState(StateConfigRequestDto request);

    List<DistrictConfigResponseDto> getAllDistrictsByState(Long stateId);

    void addDistrictToState(Long stateId, DistrictConfigRequestDto request);
}
