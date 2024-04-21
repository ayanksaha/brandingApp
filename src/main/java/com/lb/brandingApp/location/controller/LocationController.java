package com.lb.brandingApp.location.controller;

import com.lb.brandingApp.location.data.models.response.DistrictConfigResponseDto;
import com.lb.brandingApp.location.data.models.response.StateConfigResponseDto;
import com.lb.brandingApp.location.service.LocationConfigService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController {
    @Autowired
    private LocationConfigService locationConfigService;

    @GetMapping("/app/config/states")
    public ResponseEntity<List<StateConfigResponseDto>> getAllStates(
            @RequestParam(value = "category_id", required = false) Long categoryId){
        return ResponseEntity.ok(locationConfigService.getAllStates(categoryId));
    }

    @GetMapping("/app/config/state/{state_config_id}/districts")
    public ResponseEntity<List<DistrictConfigResponseDto>> getAllDistrictsByState(
            @NonNull @PathVariable("state_config_id") Long stateConfigId,
            @RequestParam(value = "state_id", required = false) Long stateId,
            @RequestParam(value = "category_id", required = false) Long categoryId) {
        return ResponseEntity.ok(locationConfigService.getAllDistrictsByState(categoryId, stateConfigId, stateId));
    }

}
