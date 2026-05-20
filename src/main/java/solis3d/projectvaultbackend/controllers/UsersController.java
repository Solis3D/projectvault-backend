package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.ValidationException;
import solis3d.projectvaultbackend.payloads.CurrentUserDTO;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.payloads.UpdateUserDTO;
import solis3d.projectvaultbackend.services.AppUsersService;
import solis3d.projectvaultbackend.services.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final ProjectService projectService;
    private final AppUsersService appUsersService;

    public UsersController(ProjectService projectService, AppUsersService appUsersService) {
        this.projectService = projectService;
        this.appUsersService = appUsersService;
    }

    @GetMapping("/me")
    public CurrentUserDTO getProfile(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return this.appUsersService.mapToDTO(currentUser);
    }

    @GetMapping("/me/projects")
    public List<ProjectRespDTO> findMyProjects(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return this.projectService.findByOwner(currentUser);
    }

    @PutMapping("/me")
    public CurrentUserDTO updateProfile(@RequestBody @Validated UpdateUserDTO body,
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

        return this.appUsersService.updateProfile(currentUser, body);
    }

    @PostMapping("/me/avatar")
    @ResponseStatus(HttpStatus.OK)
    public CurrentUserDTO uploadAvatar(@RequestParam("file")MultipartFile file, Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();

        return this.appUsersService.uploadAvatar(currentUser, file);
    }
}
