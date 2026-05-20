package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.ProjectVisibility;
import solis3d.projectvaultbackend.payloads.AdminStatsDTO;
import solis3d.projectvaultbackend.payloads.CurrentUserDTO;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.services.AppUsersService;
import solis3d.projectvaultbackend.services.ProjectService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AppUsersService appUsersService;
    private final ProjectService projectService;

    public AdminController(AppUsersService appUsersService, ProjectService projectService) {
        this.appUsersService = appUsersService;
        this.projectService = projectService;
    }

    @GetMapping("/users")
    public List<CurrentUserDTO> findAllUsers() {
        return this.appUsersService.findAll();
    }

    @GetMapping("/projects")
    public List<ProjectRespDTO> findAllProjects() {
        return this.projectService.findAllForAdmin();
    }

    @PatchMapping("/projects/{projectId}/featured")
    public ProjectRespDTO toggleFeeatured(@PathVariable UUID projectId){
        return this.projectService.toggleFeatured(projectId);
    }

    @DeleteMapping("/projects/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID projectId) {
        this.projectService.deleteForAdmin(projectId);
    }

    @GetMapping("/stats")
    public AdminStatsDTO getStats() {
        return new AdminStatsDTO(
                this.appUsersService.countAll(),
                this.projectService.countAll(),
                this.projectService.countByVisibility(ProjectVisibility.PUBLIC),
                this.projectService.countByVisibility(ProjectVisibility.PRIVATE)
        );
    }
}
