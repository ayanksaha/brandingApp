package com.lb.brandingApp.task.controller;

import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.task.data.models.request.TaskRequestDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import com.lb.brandingApp.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/app/category/{category_id}/state/{state_id}/district/{district_id}/tasks")
    public ResponseEntity<PageResponseDto<TaskResponseDto>> getAllTasks(
            @PathVariable("category_id") Long categoryId, @PathVariable("state_id") Long stateId,
            @PathVariable("district_id") Long districtId,
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder) {
        return ResponseEntity.ok(taskService.getAllTasks(
                categoryId, stateId, districtId, pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/app/team/tasks")
    public ResponseEntity<PageResponseDto<TaskResponseDto>> getAllTasksByTeam(
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder
    ) {
        return ResponseEntity.ok(taskService.getAllTasksByTeam(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/app/user/tasks")
    public ResponseEntity<PageResponseDto<TaskResponseDto>> getAllTasksByUser(
            @RequestParam(value = "page_number", required = false) Integer pageNumber,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder
    ) {
        return ResponseEntity.ok(taskService.getAllTasksByUser(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/app/task/{task_id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable("task_id") Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @PostMapping("/app/task")
    public ResponseEntity<Void> addTask(@RequestBody TaskRequestDto request) {
        log.info("addTask called with task name: {}", request.name());
        taskService.addTask(request, false, null);
        log.info("addTask successful for task name: {}", request.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/app/task/{task_id}/renew")
    public ResponseEntity<Void> renewTask(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        log.info("renewTask called with task id: {}", taskId);
        taskService.renew(request, taskId);
        log.info("renewTask successful for task id: {}", taskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/task/{task_id}")
    public ResponseEntity<Void> updateTask(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        log.info("updateTask called for task id: {}", taskId);
        taskService.updateTask(taskId, request);
        log.info("updateTask successful for task id: {}", taskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/task/{task_id}/approve")
    public ResponseEntity<Void> approveTask(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        log.info("approveTask called for task id: {}", taskId);
        taskService.approve(taskId, request);
        log.info("approveTask successful for task id: {}", taskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/task/{task_id}/next")
    public ResponseEntity<Void> assignToNextTeam(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        log.info("assignToNextTeam called for task id: {}", taskId);
        taskService.assignToNextTeam(taskId, request);
        log.info("assignToNextTeam successful for task id: {}", taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/app/task/{task_id}/images")
    public ResponseEntity<Void> addImages(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        log.info("addImages called for task id: {}", taskId);
        taskService.addImages(taskId, request);
        log.info("addImages successful for task id: {}", taskId);
        return ResponseEntity.ok().build();
    }
}