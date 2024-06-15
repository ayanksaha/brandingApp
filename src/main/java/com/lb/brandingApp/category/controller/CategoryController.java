package com.lb.brandingApp.category.controller;

import com.lb.brandingApp.category.data.models.request.CategoryRequestDto;
import com.lb.brandingApp.category.data.models.request.DistrictRequestDto;
import com.lb.brandingApp.category.data.models.request.StateRequestDto;
import com.lb.brandingApp.category.data.models.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.models.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.category.data.models.response.StateResponseDto;
import com.lb.brandingApp.category.service.CategoryService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/app/categories")
    public ResponseEntity<PageResponseDto<CategoryResponseDto>> fetchAllCategories(
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder,
            @RequestParam(value = "q", required = false) String name) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortOrder, sortBy, name));
    }

    @GetMapping("/app/category/{category_id}")
    public ResponseEntity<CategoryResponseDto> fetchCategory(@PathVariable(value = "category_id") Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    @PostMapping("/app/category")
    public ResponseEntity<Void> addCategory(@RequestBody CategoryRequestDto request) {
        categoryService.addNewCategory(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/category/{category_id}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable("category_id") @NonNull Long categoryId,
            @RequestBody CategoryRequestDto request) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/category/{category_id}/states")
    public ResponseEntity<PageResponseDto<StateResponseDto>> getAllStates(
            @PathVariable("category_id") @NonNull Long categoryId,
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder) {
        return ResponseEntity.ok(categoryService.getStatesByCategory(categoryId, pageNumber, pageSize, sortOrder, sortBy));
    }

    @GetMapping("/app/category/{category_id}/state/{state_id}")
    public ResponseEntity<StateResponseDto> fetchState(
            @PathVariable(value = "category_id") Long categoryId,
            @PathVariable(value = "state_id") Long stateId) {
        return ResponseEntity.ok(categoryService.getState(categoryId, stateId));
    }

    @PostMapping("/app/category/{category_id}/state")
    public ResponseEntity<Void> addStateToCategory(
            @RequestBody StateRequestDto request, @PathVariable("category_id") @NonNull Long categoryId) {
        categoryService.addStateToCategory(request, categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/category/{category_id}/state/{state_id}/districts")
    public ResponseEntity<PageResponseDto<DistrictResponseDto>> getAllDistricts(
            @PathVariable("category_id") @NonNull Long categoryId, @PathVariable("state_id") @NonNull Long stateId,
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder) {
        return ResponseEntity.ok(categoryService.getAllDistrictsByState(categoryId, stateId, pageNumber, pageSize, sortOrder, sortBy));
    }

    @GetMapping("/app/category/{category_id}/state/{state_id}/district/{district_id}")
    public ResponseEntity<DistrictResponseDto> fetchDistrict(
            @PathVariable(value = "category_id") Long categoryId,
            @PathVariable(value = "state_id") Long stateId,
            @PathVariable(value = "district_id") Long districtId) {
        return ResponseEntity.ok(categoryService.getDistrict(categoryId, stateId, districtId));
    }

    @PostMapping("/app/category/{category_id}/state/{state_id}/district")
    public ResponseEntity<Void> addDistrictToCategory(
            @RequestBody DistrictRequestDto request,
            @PathVariable("category_id") @NonNull Long categoryId, @PathVariable("state_id") @NonNull Long stateId) {
        categoryService.addDistrictToCategory(request, categoryId, stateId);
        return ResponseEntity.ok().build();
    }

}
