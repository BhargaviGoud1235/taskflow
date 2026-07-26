package com.bhargavi.taskflow.service.impl;

import com.bhargavi.taskflow.dto.TaskDTO;
import com.bhargavi.taskflow.exception.ResourceNotFoundException;
import com.bhargavi.taskflow.model.Project;
import com.bhargavi.taskflow.model.Task;
import com.bhargavi.taskflow.model.TaskStatus;
import com.bhargavi.taskflow.model.User;
import com.bhargavi.taskflow.repository.ProjectRepository;
import com.bhargavi.taskflow.repository.TaskRepository;
import com.bhargavi.taskflow.repository.UserRepository;
import com.bhargavi.taskflow.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + taskDTO.getProjectId()));

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setProject(project);
        task.setStatus(taskDTO.getStatus() != null ? TaskStatus.valueOf(taskDTO.getStatus()) : TaskStatus.TODO);
        task.setPriority(taskDTO.getPriority() != null
                ? com.bhargavi.taskflow.model.TaskPriority.valueOf(taskDTO.getPriority())
                : com.bhargavi.taskflow.model.TaskPriority.MEDIUM);

        if (taskDTO.getAssignedToId() != null) {
            User assignee = userRepository.findById(taskDTO.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getAssignedToId()));
            task.setAssignedTo(assignee);
        }

        Task saved = taskRepository.save(task);
        return toDTO(saved);
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toDTO(task);
    }

    @Override
    public List<TaskDTO> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksAssignedToUser(Long userId) {
        return taskRepository.findByAssignedToId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getPriority() != null) {
            task.setPriority(com.bhargavi.taskflow.model.TaskPriority.valueOf(taskDTO.getPriority()));
        }

        Task updated = taskRepository.save(task);
        return toDTO(updated);
    }

    @Override
    public TaskDTO updateStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setStatus(TaskStatus.valueOf(status.toUpperCase()));
        Task updated = taskRepository.save(task);
        return toDTO(updated);
    }

    @Override
    public TaskDTO assignTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        task.setAssignedTo(user);
        Task updated = taskRepository.save(task);
        return toDTO(updated);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getPriority().name(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getName() : null
        );
    }
}
