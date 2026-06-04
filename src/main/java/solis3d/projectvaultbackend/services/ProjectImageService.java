package solis3d.projectvaultbackend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.entities.Project;
import solis3d.projectvaultbackend.entities.ProjectImage;
import solis3d.projectvaultbackend.entities.ProjectVisibility;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.CloudinaryUploadRespDTO;
import solis3d.projectvaultbackend.payloads.NewProjectImageDTO;
import solis3d.projectvaultbackend.payloads.ProjectImageRespDTO;
import solis3d.projectvaultbackend.payloads.UpdateProjectImageDTO;
import solis3d.projectvaultbackend.repositories.ProjectImageRepository;
import org.springframework.web.multipart.MultipartFile;
import solis3d.projectvaultbackend.entities.ProjectImageType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectImageService {
    private final ProjectImageRepository projectImageRepository;
    private final ProjectService projectService;
    private final CloudinaryService cloudinaryService;

    public ProjectImageService(ProjectImageRepository projectImageRepository, ProjectService projectService,  CloudinaryService cloudinaryService) {
        this.projectImageRepository = projectImageRepository;
        this.projectService = projectService;
        this.cloudinaryService = cloudinaryService;
    }

    public List<ProjectImageRespDTO> findByPublicProject(UUID projectId){
        Project project = this.projectService.findById(projectId);

        if(project.getProjectVisibility() != ProjectVisibility.PUBLIC) {
            throw new NotFoundException("Progetto con id " + projectId + " non trovato!");
        }

        return this.projectImageRepository.findByProject_IdOrderBySortOrderAsc(projectId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private ProjectImage findById(UUID imageId) {
        return this.projectImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Immagine con id " + imageId + " non trovata!"));
    }

    public List<ProjectImageRespDTO> findByProject(UUID projectId, AppUser currentUser) {
        Project project = this.projectService.findById(projectId);

        this.projectService.checkOwnershipOrAdmin(project, currentUser);

        return this.projectImageRepository.findByProject_IdOrderBySortOrderAsc(projectId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public ProjectImageRespDTO save (UUID projectId, NewProjectImageDTO body, AppUser currentUser) {
        Project project = this.projectService.findById(projectId);

        this.projectService.checkOwnershipOrAdmin(project, currentUser);

        ProjectImage newProjectImage = new ProjectImage();
        newProjectImage.setImageUrl(body.imageUrl());
        newProjectImage.setCaption(body.caption());
        newProjectImage.setImageType(body.imageType());
        newProjectImage.setStageLabel(body.stageLabel());
        newProjectImage.setSortOrder(body.sortOrder());
        newProjectImage.setCreatedAt(LocalDateTime.now());
        newProjectImage.setProject(project);

        return this.mapToDTO(projectImageRepository.save(newProjectImage));

    }

    @Transactional
    public ProjectImageRespDTO uploadAndSave(UUID projectId, MultipartFile file, String caption, ProjectImageType imageType, String stageLabel, Integer sortOrder, AppUser currentUser) {
        Project project = this.projectService.findById(projectId);

        this.projectService.checkOwnershipOrAdmin(project, currentUser);

        CloudinaryUploadRespDTO uploadedImage = this.cloudinaryService.uploadImage(file);

        ProjectImage newProjectImage = new ProjectImage();
        newProjectImage.setImageUrl(uploadedImage.imageUrl());
        newProjectImage.setCloudinaryPublicId(uploadedImage.publicId());
        newProjectImage.setCaption(caption);
        newProjectImage.setImageType(imageType);
        newProjectImage.setStageLabel(stageLabel);
        newProjectImage.setSortOrder(sortOrder);
        newProjectImage.setCreatedAt(LocalDateTime.now());
        newProjectImage.setProject(project);

        return this.mapToDTO(projectImageRepository.save(newProjectImage));
    }

    @Transactional
    public ProjectImageRespDTO update(UUID imageId, UpdateProjectImageDTO body, AppUser currentUser) {
        ProjectImage foundImage = this.findById(imageId);

        this.projectService.checkOwnershipOrAdmin(foundImage.getProject(), currentUser);

        if(body.imageUrl() != null) {
            foundImage.setImageUrl(body.imageUrl());
            foundImage.setCloudinaryPublicId(null);
        }

        if(body.caption() != null) {
            foundImage.setCaption(body.caption());
        }

        if(body.imageType() != null) {
            foundImage.setImageType(body.imageType());
        }

        if(body.stageLabel() != null) {
            foundImage.setStageLabel(body.stageLabel());
        }

        if(body.sortOrder() != null) {
            foundImage.setSortOrder(body.sortOrder());
        }

        foundImage.setUpdatedAt(LocalDateTime.now());

        return this.mapToDTO(projectImageRepository.save(foundImage));
    }

    @Transactional
    public void delete(UUID imageId, AppUser currentUser) {
        ProjectImage foundImage = this.findById(imageId);

        this.projectService.checkOwnershipOrAdmin(foundImage.getProject(), currentUser);

        this.deleteCloudinaryImage(foundImage);
        this.projectImageRepository.delete(foundImage);
    }

    private void deleteCloudinaryImage(ProjectImage projectImage) {
        if(projectImage.getCloudinaryPublicId() != null) {
            this.cloudinaryService.deleteImage(projectImage.getCloudinaryPublicId());
        } else {
            this.cloudinaryService.deleteImageByUrl(projectImage.getImageUrl());
        }
    }

    private ProjectImageRespDTO mapToDTO(ProjectImage projectImage) {
        return new ProjectImageRespDTO(
                projectImage.getId(),
                projectImage.getImageUrl(),
                projectImage.getCaption(),
                projectImage.getImageType(),
                projectImage.getStageLabel(),
                projectImage.getSortOrder(),
                projectImage.getCreatedAt(),
                projectImage.getUpdatedAt(),
                projectImage.getProject().getId()
        );
    }

}
