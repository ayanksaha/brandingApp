package com.lb.brandingApp.config.controller;

import com.lb.brandingApp.config.data.dto.request.DistrictConfigRequestDto;
import com.lb.brandingApp.config.data.dto.request.ProductConfigRequestDto;
import com.lb.brandingApp.config.data.dto.request.StateConfigRequestDto;
import com.lb.brandingApp.config.data.dto.response.DistrictConfigResponseDto;
import com.lb.brandingApp.config.data.dto.response.ProductConfigResponseDto;
import com.lb.brandingApp.config.data.dto.response.StateConfigResponseDto;
import com.lb.brandingApp.config.service.ProductConfigService;
import com.lb.brandingApp.config.service.StateConfigService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ConfigController {
    @Autowired
    private ProductConfigService productConfigService;

    @Autowired
    private StateConfigService stateConfigService;

    @GetMapping("/app/config/products")
    public ResponseEntity<List<ProductConfigResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productConfigService.getAllProductConfigs());
    }

    @PostMapping("/app/config/product")
    public ResponseEntity<Void> addProduct(@RequestBody ProductConfigRequestDto request) {
        productConfigService.addProductConfig(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/config/product/{product_id}")
    public ResponseEntity<Void> updateProduct(@NonNull @PathVariable("product_id") Long productId,
            @RequestBody ProductConfigRequestDto request) {
        productConfigService.updateProductConfig(productId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/config/states")
    public ResponseEntity<List<StateConfigResponseDto>> getAllStates() {
        return ResponseEntity.ok(stateConfigService.getAllStates());
    }

    @PostMapping("/app/config/state")
    public ResponseEntity<Void> addState(@RequestBody StateConfigRequestDto request) {
        stateConfigService.addState(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/config/state/{state_id}/districts")
    public ResponseEntity<List<DistrictConfigResponseDto>> getAllDistrictsByState(
            @NonNull @PathVariable("state_id") Long stateId) {
        return ResponseEntity.ok(stateConfigService.getAllDistrictsByState(stateId));
    }

    @PostMapping("/app/config/state/{state_id}/district")
    public ResponseEntity<Void> addDistrict(@NonNull @PathVariable("state_id") Long stateId,
            @RequestBody DistrictConfigRequestDto request) {
        stateConfigService.addDistrictToState(stateId, request);
        return ResponseEntity.ok().build();
    }

}
