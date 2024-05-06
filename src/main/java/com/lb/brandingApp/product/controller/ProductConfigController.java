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

    @GetMapping("/app/category/{category_id}/config/products")
    public ResponseEntity<List<ProductConfigResponseDto>> getAllProducts(
            @NonNull @PathVariable("category_id") Long categoryId) {
        return ResponseEntity.ok(productConfigService.getAllProductConfigs(categoryId));
    }

    @PostMapping("/app/category/{category_id}/config/product")
    public ResponseEntity<Void> addProduct(
            @NonNull @PathVariable("category_id") Long categoryId, @RequestBody ProductConfigRequestDto request) {
        productConfigService.addProductConfig(categoryId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/category/{category_id}/config/product/{product_id}")
    public ResponseEntity<Void> updateProduct(
            @NonNull @PathVariable("category_id") Long categoryId, @NonNull @PathVariable("product_id") Long productId,
            @RequestBody ProductConfigRequestDto request) {
        productConfigService.updateProductConfig(productId, categoryId, request);
        return ResponseEntity.ok().build();
    }

}
