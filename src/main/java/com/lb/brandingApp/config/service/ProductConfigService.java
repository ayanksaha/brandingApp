package com.lb.brandingApp.config.service;

import com.lb.brandingApp.config.data.dto.request.ProductConfigRequestDto;
import com.lb.brandingApp.config.data.dto.response.ProductConfigResponseDto;
import java.util.List;

public interface ProductConfigService {

    List<ProductConfigResponseDto> getAllProductConfigs();

    void addProductConfig(ProductConfigRequestDto request);

    void updateProductConfig(Long productId, ProductConfigRequestDto request);
}
