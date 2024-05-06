package com.lb.brandingApp.task.controller;

import com.lb.brandingApp.category.data.models.response.PageResponseDto;
import com.lb.brandingApp.task.data.models.request.TaskRequestDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import com.lb.brandingApp.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        taskService.addTask(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/task/{task_id}")
    public ResponseEntity<Void> updateTask(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        taskService.updateTask(taskId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/app/task/{task_id}/approve")
    public ResponseEntity<Void> approveTaskAndAssignToNextTeam(@PathVariable("task_id") Long taskId, @RequestBody TaskRequestDto request) {
        taskService.approveAndAssignNext(taskId, request);
        return ResponseEntity.ok().build();
    }
}