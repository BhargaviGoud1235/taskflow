package com.bhargavi.taskflow.service;

import com.bhargavi.taskflow.dto.ProjectDTO;

import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(ProjectDTO projectDTO, String ownerEmail);
    ProjectDTO getProjectById(Long id);
    List<ProjectDTO> getMyProjects(String ownerEmail);
    List<ProjectDTO> getAllProjects();
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO, String requesterEmail);
    void deleteProject(Long id, String requesterEmail);
}
