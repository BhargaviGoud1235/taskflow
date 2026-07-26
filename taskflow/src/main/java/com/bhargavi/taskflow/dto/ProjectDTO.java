package com.bhargavi.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;
    private LocalDate createdDate;
    private Long ownerId;
    private String ownerName;
}
