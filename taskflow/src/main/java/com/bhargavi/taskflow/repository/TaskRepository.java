package com.bhargavi.taskflow.repository;

import com.bhargavi.taskflow.model.Task;
import com.bhargavi.taskflow.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
}
