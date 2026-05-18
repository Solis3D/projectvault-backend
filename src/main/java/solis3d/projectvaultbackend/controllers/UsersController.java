package solis3d.projectvaultbackend.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.payloads.CurrentUserDTO;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.services.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final ProjectService projectService;

    public UsersController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/me")
    public CurrentUserDTO getProfile(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return new CurrentUserDTO(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getRole(),
                currentUser.getAvatarUrl(),
                currentUser.getPosition(),
                currentUser.getBio()
        );
    }

    @GetMapping("/me/projects")
    public List<ProjectRespDTO> findMyProjects(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return this.projectService.findByOwner(currentUser);
    }
}
