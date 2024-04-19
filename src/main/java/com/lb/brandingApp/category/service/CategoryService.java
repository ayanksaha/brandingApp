package com.lb.brandingApp.category.service;

import com.lb.brandingApp.category.data.dto.request.CategoryRequestDto;
import com.lb.brandingApp.category.data.dto.request.DistrictRequestDto;
import com.lb.brandingApp.category.data.dto.request.StateRequestDto;
import com.lb.brandingApp.category.data.dto.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.dto.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.dto.response.StateResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getAllCategories();
    void addNewCategory(CategoryRequestDto request);

    void updateCategory(Long categoryId, CategoryRequestDto request);

    List<StateResponseDto> getStatesByCategory(Long categoryId);

    void addStateToCategory(StateRequestDto request, Long categoryId);

    List<DistrictResponseDto> getAllDistrictsByState(Long categoryId, Long stateId);

    void addDistrictToCategory(DistrictRequestDto request, Long categoryId, Long stateId);
}
