package com.bhargavi.taskflow.controller;

import com.bhargavi.taskflow.dto.ProjectDTO;
import com.bhargavi.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO, Authentication auth) {
        return new ResponseEntity<>(projectService.createProject(projectDTO, auth.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ProjectDTO>> getMyProjects(Authentication auth) {
        return ResponseEntity.ok(projectService.getMyProjects(auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO, Authentication auth) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO, auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication auth) {
        projectService.deleteProject(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
