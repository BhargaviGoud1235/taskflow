package com.bhargavi.taskflow.service.impl;

import com.bhargavi.taskflow.dto.ProjectDTO;
import com.bhargavi.taskflow.exception.AccessDeniedCustomException;
import com.bhargavi.taskflow.exception.ResourceNotFoundException;
import com.bhargavi.taskflow.model.Project;
import com.bhargavi.taskflow.model.User;
import com.bhargavi.taskflow.repository.ProjectRepository;
import com.bhargavi.taskflow.repository.UserRepository;
import com.bhargavi.taskflow.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));

        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setOwner(owner);

        Project saved = projectRepository.save(project);
        return toDTO(saved);
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return toDTO(project);
    }

    @Override
    public List<ProjectDTO> getMyProjects(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));
        return projectRepository.findByOwnerId(owner.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO, String requesterEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (!project.getOwner().getEmail().equals(requesterEmail)) {
            throw new AccessDeniedCustomException("Only the project owner can update this project");
        }

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());

        Project updated = projectRepository.save(project);
        return toDTO(updated);
    }

    @Override
    public void deleteProject(Long id, String requesterEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (!project.getOwner().getEmail().equals(requesterEmail)) {
            throw new AccessDeniedCustomException("Only the project owner can delete this project");
        }

        projectRepository.delete(project);
    }

    private ProjectDTO toDTO(Project project) {
        return new ProjectDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedDate(),
                project.getOwner().getId(),
                project.getOwner().getName()
        );
    }
}
