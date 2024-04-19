package com.lb.brandingApp.product.controller;

import com.lb.brandingApp.product.data.models.request.ProductConfigRequestDto;
import com.lb.brandingApp.product.data.models.response.ProductConfigResponseDto;
import com.lb.brandingApp.product.service.ProductConfigService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductConfigController {
    @Autowired
    private ProductConfigService productConfigService;

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

}
