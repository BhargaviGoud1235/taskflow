package com.bhargavi.taskflow.service;

import com.bhargavi.taskflow.dto.TaskDTO;

import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO getTaskById(Long id);
    List<TaskDTO> getTasksByProject(Long projectId);
    List<TaskDTO> getTasksAssignedToUser(Long userId);
    TaskDTO updateTask(Long id, TaskDTO taskDTO);
    TaskDTO updateStatus(Long id, String status);
    TaskDTO assignTask(Long taskId, Long userId);
    void deleteTask(Long id);
}
