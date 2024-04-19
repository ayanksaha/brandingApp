package com.lb.brandingApp.task.service;

import com.lb.brandingApp.task.data.dto.request.TaskRequestDto;
import com.lb.brandingApp.task.data.dto.response.TaskResponseDto;

import java.util.List;

public interface TaskService {
    List<TaskResponseDto> getAllTasks(Long categoryId, Long stateId, Long districtId);
    List<TaskResponseDto> getAllTasksByTeam();
    void addTask(TaskRequestDto request);
    TaskResponseDto getTaskById(Long taskId);
}