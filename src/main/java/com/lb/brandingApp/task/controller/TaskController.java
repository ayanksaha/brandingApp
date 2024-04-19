package com.lb.brandingApp.task.controller;

import com.lb.brandingApp.task.data.models.request.TaskRequestDto;
import com.lb.brandingApp.task.data.models.response.TaskResponseDto;
import com.lb.brandingApp.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/app/category/{category_id}/state/{state_id}/district/{district_id}/tasks")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(@PathVariable("category_id") Long categoryId,
             @PathVariable("state_id") Long stateId, @PathVariable("district_id") Long districtId) {
        return ResponseEntity.ok(taskService.getAllTasks(categoryId, stateId, districtId));
    }

    @GetMapping("/app/tasks")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksByTeam() {
        return ResponseEntity.ok(taskService.getAllTasksByTeam());
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
}