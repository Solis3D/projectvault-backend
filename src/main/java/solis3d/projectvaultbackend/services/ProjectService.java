package solis3d.projectvaultbackend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.*;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.exceptions.UnauthorizedException;
import solis3d.projectvaultbackend.payloads.NewProjectDTO;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.payloads.SoftwareRespDTO;
import solis3d.projectvaultbackend.payloads.UpdateProjectDTO;
import solis3d.projectvaultbackend.repositories.ProjectRepository;
import solis3d.projectvaultbackend.repositories.ProjectSoftwareRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectSoftwareRepository projectSoftwareRepository;
    private final CategoryService categoryService;
    private final SoftwareService softwareService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectSoftwareRepository projectSoftwareRepository,
                          CategoryService categoryService,
                          SoftwareService softwareService) {
        this.projectRepository = projectRepository;
        this.projectSoftwareRepository = projectSoftwareRepository;
        this.categoryService = categoryService;
        this.softwareService = softwareService;
    }

    public Page<ProjectRespDTO> findAllPublic(String title, UUID categoryId, Pageable pageable) {
        Page<Project> projects;

        if(title != null && categoryId != null) {
            projects = this.projectRepository.findByProjectVisibilityAndTitleContainingIgnoreCaseAndCategory_Id(
                    ProjectVisibility.PUBLIC, title, categoryId, pageable
            );
        } else if (title != null) {
            projects = this.projectRepository.findByProjectVisibilityAndTitleContainingIgnoreCase(
                    ProjectVisibility.PUBLIC, title, pageable
            );
        } else if (categoryId != null) {
            projects = this.projectRepository.findByProjectVisibilityAndCategory_Id(
                    ProjectVisibility.PUBLIC, categoryId, pageable
            );
        } else {
            projects = this.projectRepository.findByProjectVisibility(ProjectVisibility.PUBLIC, pageable);
        }

        return projects.map(this::mapToDTO);
    }

    public ProjectRespDTO findPublicById(UUID projectId) {
        Project foundProject = this.findById(projectId);

        if(foundProject.getProjectVisibility() != ProjectVisibility.PUBLIC) {
            throw new NotFoundException("Progetto con id " + projectId + " non trovato!");
        }

        return this.mapToDTO(foundProject);
    }

    public Page<ProjectRespDTO> findByOwner(AppUser currentUser, Pageable pageable) {
        return this.projectRepository.findByOwner_Id(currentUser.getId(), pageable)
                .map(this::mapToDTO);
    }

    @Transactional
    public ProjectRespDTO save(NewProjectDTO body, AppUser currentUser) {
        Category category = this.categoryService.findById(body.categoryId());

        Project newProject = new Project();
        newProject.setTitle(body.title());
        newProject.setDescription(body.description());
        newProject.setTechnicalNotes(body.technicalNotes());
        newProject.setProjectStatus(body.projectStatus());
        newProject.setProjectVisibility(body.projectVisibility());
        newProject.setThumbnailUrl(body.thumbnailUrl());
        newProject.setModelUrl(body.modelUrl());
        newProject.setFeatured(false);
        newProject.setCreatedAt(LocalDateTime.now());
        newProject.setOwner(currentUser);
        newProject.setCategory(category);

        Project savedProject = this.projectRepository.save(newProject);

        this.addSoftwaresToProject(savedProject, body.softwareIds());

        return this.mapToDTO(savedProject);
    }

    @Transactional
    public ProjectRespDTO update(UUID projectId, UpdateProjectDTO body, AppUser currentUser) {
        Project foundProject = this.findById(projectId);

        this.checkOwnershipOrAdmin(foundProject, currentUser);

        if(body.title() != null) {
            foundProject.setTitle(body.title());
        }

        if(body.description() != null) {
            foundProject.setDescription(body.description());
        }

        if(body.technicalNotes() != null) {
            foundProject.setTechnicalNotes(body.technicalNotes());
        }

        if(body.projectStatus() != null) {
            foundProject.setProjectStatus(body.projectStatus());
        }

        if(body.projectVisibility() != null) {
            foundProject.setProjectVisibility(body.projectVisibility());
        }

        if(body.thumbnailUrl() != null) {
            foundProject.setThumbnailUrl(body.thumbnailUrl());
        }

        if(body.modelUrl() != null) {
            foundProject.setModelUrl(body.modelUrl());
        }

        if(body.categoryId() != null) {
            Category category = this.categoryService.findById(body.categoryId());
            foundProject.setCategory(category);
        }

        foundProject.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = this.projectRepository.save(foundProject);

        if(body.softwareIds() != null) {
            this.projectSoftwareRepository.deleteByProject_Id(updatedProject.getId());
            this.addSoftwaresToProject(updatedProject, body.softwareIds());
        }

        return this.mapToDTO(updatedProject);
    }

    @Transactional
    public void delete(UUID projectId, AppUser currentUser) {
        Project foundProject = this.findById(projectId);

        this.checkOwnershipOrAdmin(foundProject, currentUser);

        this.projectSoftwareRepository.deleteByProject_Id(projectId);
        this.projectRepository.delete(foundProject);
    }

    public Project findById(UUID projectId) {
        return this.projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Progetto con id " + projectId + " non trovato!"));
    }

    public void checkOwnershipOrAdmin(Project project, AppUser currentUser) {
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if(!isOwner && !isAdmin) {
            throw new UnauthorizedException("Non puoi modificare o eliminare questo progetto!");
        }
    }

    public Page<ProjectRespDTO> findAllForAdmin(Pageable pageable) {
        return this.projectRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Transactional public ProjectRespDTO toggleFeatured(UUID projectId) {
        Project foundProject = this.findById(projectId);

        foundProject.setFeatured(!foundProject.isFeatured());
        foundProject.setUpdatedAt(LocalDateTime.now());

        return this.mapToDTO(this.projectRepository.save(foundProject));
    }

    @Transactional
    public void deleteForAdmin(UUID projectId) {
        Project foundProject = this.findById(projectId);

        this.projectSoftwareRepository.deleteByProject_Id(projectId);
        this.projectRepository.delete(foundProject);
    }

    public long countAll() {
        return this.projectRepository.count();
    }

    public long countByVisibility(ProjectVisibility projectVisibility) {
        return this.projectRepository.countByProjectVisibility(projectVisibility);
    }

    private void addSoftwaresToProject(Project project, List<UUID> softwareIds) {
        if(softwareIds == null || softwareIds.isEmpty()) {
            return;
        }

        softwareIds.forEach(softwareId -> {
            if(!this.projectSoftwareRepository.existsByProject_IdAndSoftware_Id(project.getId(), softwareId)) {
                Software software = this.softwareService.findById(softwareId);

                ProjectSoftware projectSoftware = new ProjectSoftware();
                projectSoftware.setProject(project);
                projectSoftware.setSoftware(software);
                projectSoftware.setCreatedAt(LocalDateTime.now());

                this.projectSoftwareRepository.save(projectSoftware);
            }
        });
    }

    private List<SoftwareRespDTO> getProjectSoftwares(UUID projectId) {
        return this.projectSoftwareRepository.findByProject_Id(projectId)
                .stream()
                .map(projectSoftware -> new SoftwareRespDTO(
                        projectSoftware.getSoftware().getId(),
                        projectSoftware.getSoftware().getName(),
                        projectSoftware.getSoftware().getSlug(),
                        projectSoftware.getSoftware().getIconUrl()
                ))
                .toList();
    }

    private ProjectRespDTO mapToDTO(Project project) {
        return new ProjectRespDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getTechnicalNotes(),
                project.getProjectStatus(),
                project.getProjectVisibility(),
                project.getThumbnailUrl(),
                project.getModelUrl(),
                project.isFeatured(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getOwner().getId(),
                project.getOwner().getUsername(),
                project.getOwner().getAvatarUrl(),
                project.getCategory().getId(),
                project.getCategory().getName(),
                this.getProjectSoftwares(project.getId())
        );
    }
}