package solis3d.projectvaultbackend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.ProjectVisibility;
import solis3d.projectvaultbackend.payloads.AdminStatsDTO;
import solis3d.projectvaultbackend.payloads.CurrentUserDTO;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.services.AppUsersService;
import solis3d.projectvaultbackend.services.ProjectService;

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
    public Page<CurrentUserDTO> findAllUsers(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return this.appUsersService.findAll(pageable);
    }

    @GetMapping("/projects")
    public Page<ProjectRespDTO> findAllProjects(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return this.projectService.findAllForAdmin(pageable);
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
