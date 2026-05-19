package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.ValidationException;
import solis3d.projectvaultbackend.payloads.NewProjectImageDTO;
import solis3d.projectvaultbackend.payloads.ProjectImageRespDTO;
import solis3d.projectvaultbackend.payloads.UpdateProjectImageDTO;
import solis3d.projectvaultbackend.services.ProjectImageService;
import org.springframework.web.multipart.MultipartFile;
import solis3d.projectvaultbackend.entities.ProjectImageType;

import java.util.List;
import java.util.UUID;

@RestController
public class ProjectImageController {

    private final ProjectImageService projectImageService;

    public ProjectImageController(ProjectImageService projectImageService) {
        this.projectImageService = projectImageService;
    }

    @GetMapping("/projects/{projectId}/images")
    public List<ProjectImageRespDTO> findByPublicProject(@PathVariable UUID projectId) {
        return this.projectImageService.findByPublicProject(projectId);
    }

    @GetMapping("/users/me/projects/{projectId}/images")
    public List<ProjectImageRespDTO> findByMyProject(@PathVariable UUID projectId, Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return this.projectImageService.findByProject(projectId, currentUser);
    }

    @PostMapping("/projects/{projectId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectImageRespDTO save(@PathVariable UUID projectId,
                                    @RequestBody @Validated NewProjectImageDTO body,
                                    BindingResult validationResult,
                                    Authentication authentication) {
        if(validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return this.projectImageService.save(projectId, body, currentUser);
    }

    @PostMapping("/projects/{projectId}/images/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectImageRespDTO uploadImage(@PathVariable UUID projectId,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(required = false) String caption,
                                           @RequestParam ProjectImageType imageType,
                                           @RequestParam(required = false) String stageLabel,
                                           @RequestParam(required = false) Integer sortOrder,
                                           Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return this.projectImageService.uploadAndSave(
                projectId,
                file,
                caption,
                imageType,
                stageLabel,
                sortOrder,
                currentUser
        );
    }

    @PutMapping("/project-images/{imageId}")
    public ProjectImageRespDTO update(@PathVariable UUID imageId,
                                      @RequestBody @Validated UpdateProjectImageDTO body,
                                      BindingResult validationResult,
                                      Authentication authentication) {
        if(validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return this.projectImageService.update(imageId, body, currentUser);
    }

    @DeleteMapping("/project-images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID imageId, Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        this.projectImageService.delete(imageId, currentUser);
    }
}
