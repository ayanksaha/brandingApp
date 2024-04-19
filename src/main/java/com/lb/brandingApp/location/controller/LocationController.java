package com.lb.brandingApp.location.controller;

import com.lb.brandingApp.location.data.models.request.DistrictConfigRequestDto;
import com.lb.brandingApp.location.data.models.request.StateConfigRequestDto;
import com.lb.brandingApp.location.data.models.response.DistrictConfigResponseDto;
import com.lb.brandingApp.location.data.models.response.StateConfigResponseDto;
import com.lb.brandingApp.location.service.LocationConfigService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {
    @Autowired
    private LocationConfigService locationConfigService;

    @GetMapping("/app/config/states")
    public ResponseEntity<List<StateConfigResponseDto>> getAllStates() {
        return ResponseEntity.ok(locationConfigService.getAllStates());
    }

    @PostMapping("/app/config/state")
    public ResponseEntity<Void> addState(@RequestBody StateConfigRequestDto request) {
        locationConfigService.addState(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/config/state/{state_id}/districts")
    public ResponseEntity<List<DistrictConfigResponseDto>> getAllDistrictsByState(
            @NonNull @PathVariable("state_id") Long stateId) {
        return ResponseEntity.ok(locationConfigService.getAllDistrictsByState(stateId));
    }

    @PostMapping("/app/config/state/{state_id}/district")
    public ResponseEntity<Void> addDistrict(@NonNull @PathVariable("state_id") Long stateId,
                                            @RequestBody DistrictConfigRequestDto request) {
        locationConfigService.addDistrictToState(stateId, request);
        return ResponseEntity.ok().build();
    }
}
