package com.lb.brandingApp.category.controller;

import com.lb.brandingApp.category.data.dto.request.CategoryRequestDto;
import com.lb.brandingApp.category.data.dto.request.DistrictRequestDto;
import com.lb.brandingApp.category.data.dto.request.StateRequestDto;
import com.lb.brandingApp.category.data.dto.response.CategoryResponseDto;
import com.lb.brandingApp.category.data.dto.response.DistrictResponseDto;
import com.lb.brandingApp.category.data.dto.response.StateResponseDto;
import com.lb.brandingApp.category.service.CategoryService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/app/categories")
    public ResponseEntity<List<CategoryResponseDto>> fetchAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/app/category")
    public ResponseEntity<Void> addCategory(@RequestBody CategoryRequestDto request){
        categoryService.addNewCategory(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/category/{category_id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("category_id") @NonNull Long categoryId,
           @RequestBody CategoryRequestDto request){
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/category/{category_id}/states")
    public ResponseEntity<List<StateResponseDto>> getAllStates(@PathVariable("category_id") @NonNull Long categoryId) {
        return ResponseEntity.ok(categoryService.getStatesByCategory(categoryId));
    }

    @PostMapping("/app/category/{category_id}/state")
    public ResponseEntity<Void> addStateToCategory(
            @RequestBody StateRequestDto request, @PathVariable("category_id") @NonNull Long categoryId) {
        categoryService.addStateToCategory(request, categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/app/category/{category_id}/state/{state_id}/districts")
    public ResponseEntity<List<DistrictResponseDto>> getAllDistricts(
            @PathVariable("category_id") @NonNull Long categoryId, @PathVariable("state_id") @NonNull Long stateId) {
        return ResponseEntity.ok(categoryService.getAllDistrictsByState(categoryId, stateId));
    }

    @PostMapping("/app/category/{category_id}/state/{state_id}/district")
    public ResponseEntity<Void> addDistrictToCategory(@RequestBody DistrictRequestDto request,
            @PathVariable("category_id") @NonNull Long categoryId, @PathVariable("state_id") @NonNull Long stateId) {
        categoryService.addDistrictToCategory(request, categoryId, stateId);
        return ResponseEntity.ok().build();
    }

}
