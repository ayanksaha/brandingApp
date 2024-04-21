package com.lb.brandingApp.product.service;

import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.product.data.entities.ProductConfig;
import com.lb.brandingApp.product.data.models.request.ProductConfigRequestDto;
import com.lb.brandingApp.product.data.models.response.ProductConfigResponseDto;
import com.lb.brandingApp.product.repository.ProductConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.lb.brandingApp.app.constants.ApplicationConstants.PRODUCT_NOT_FOUND;
import static com.lb.brandingApp.app.utils.AppUtil.mapAmount;

//Dropdown values will not be paginated.
@Service
@Transactional
public class ProductConfigService {

    @Autowired
    private ProductConfigRepository productConfigRepository;

    public List<ProductConfigResponseDto> getAllProductConfigs() {
        return productConfigRepository.findAll().stream().map(
                productConfig -> ProductConfigResponseDto.builder()
                        .name(productConfig.getName())
                        .id(productConfig.getId())
                        .unitAmount(productConfig.getAmount())
                        .build()
        ).sorted(Comparator.comparing(ProductConfigResponseDto::getName)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProductConfig(ProductConfigRequestDto request) {
        ProductConfig product = new ProductConfig();
        product.setName(request.productName());
        product.setAmount(mapAmount(request.amount().value()));
        productConfigRepository.save(product);
    }

    public void updateProductConfig(Long productId, ProductConfigRequestDto request) {
        ProductConfig product = productConfigRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
        product.setName(request.productName());
        if(Objects.nonNull(request.amount())) {
            Amount amount = product.getAmount();
            amount.setValue(request.amount().value());
            product.setAmount(amount);
        }
        productConfigRepository.save(product);
    }
}
