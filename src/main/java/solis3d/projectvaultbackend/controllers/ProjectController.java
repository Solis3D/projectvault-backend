package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.ValidationException;
import solis3d.projectvaultbackend.payloads.NewProjectDTO;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.payloads.UpdateProjectDTO;
import solis3d.projectvaultbackend.services.ProjectService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectRespDTO> findAllPublic() {
        return this.projectService.findAllPublic();
    }

    @GetMapping("/{projectId}")
    public ProjectRespDTO findPublicById(@PathVariable UUID projectId) {
        return this.projectService.findPublicById(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectRespDTO save(@RequestBody @Validated NewProjectDTO body, BindingResult validationResult, Authentication authentication) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return this.projectService.save(body, currentUser);
    }

    @PutMapping("/{projectId}")
    public ProjectRespDTO update(@PathVariable UUID projectId, @RequestBody @Validated UpdateProjectDTO body, BindingResult validationResult, Authentication authentication) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return this.projectService.update(projectId,body,currentUser);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID projectId, Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        this.projectService.delete(projectId,currentUser);
    }
}
