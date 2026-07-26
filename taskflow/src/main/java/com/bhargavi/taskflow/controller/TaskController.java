package com.bhargavi.taskflow.controller;

import com.bhargavi.taskflow.dto.TaskDTO;
import com.bhargavi.taskflow.dto.TaskStatusUpdateRequest;
import com.bhargavi.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksAssignedToUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksAssignedToUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<TaskDTO> assignTask(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignTask(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
