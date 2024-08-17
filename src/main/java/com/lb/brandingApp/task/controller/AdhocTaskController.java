package com.lb.brandingApp.task.controller;

import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.task.data.models.request.AdhocTaskRequestDto;
import com.lb.brandingApp.task.data.models.response.AdhocTaskResponseDto;
import com.lb.brandingApp.task.service.AdhocTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AdhocTaskController {

    @Autowired
    private AdhocTaskService adhocTaskService;

    @PostMapping("/app/adhoc/tasks")
    public ResponseEntity<AdhocTaskResponseDto> createAdhocTask(@RequestBody AdhocTaskRequestDto request) {
        log.info("createAdhocTask request received for {}", request.name());
        AdhocTaskResponseDto savedAdhocTask = adhocTaskService.createAdhocTask(request);
        log.info("createAdhocTask request successful for {}", savedAdhocTask.getId());
        return ResponseEntity.ok(savedAdhocTask);
    }

    @GetMapping("/app/adhoc/user/tasks")
    public ResponseEntity<PageResponseDto<AdhocTaskResponseDto>> getAllAdhocTasksByUser(
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder) {
        return ResponseEntity.ok(adhocTaskService.getAllAdhocTasksByUser(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/app/adhoc/tasks")
    public ResponseEntity<PageResponseDto<AdhocTaskResponseDto>> getAllAdhocTasks(
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder) {
        return ResponseEntity.ok(adhocTaskService.getAllAdhocTasks(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/app/adhoc/tasks/{task_id}")
    public ResponseEntity<AdhocTaskResponseDto> getAllAdhocTasksById(
            @PathVariable("task_id") Long adhocTaskId) {
        return ResponseEntity.ok(adhocTaskService.getAdhocTaskById(adhocTaskId));
    }
}
