package com.bhargavi.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long assignedToId;
    private String assignedToName;
}
