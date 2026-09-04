package solis3d.projectvaultbackend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.payloads.ProjectRespDTO;
import solis3d.projectvaultbackend.payloads.PublicArtistDTO;
import solis3d.projectvaultbackend.services.AppUsersService;
import solis3d.projectvaultbackend.services.ProjectService;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final AppUsersService appUsersService;
    private final ProjectService projectService;

    public ArtistController(
            AppUsersService appUsersService,
            ProjectService projectService
    ) {
        this.appUsersService = appUsersService;
        this.projectService = projectService;
    }

    @GetMapping("/{username}")
    public PublicArtistDTO findByUsername(
            @PathVariable String username
    ) {
        return this.appUsersService.findPublicByUsername(username);
    }

    @GetMapping("/{username}/projects")
    public Page<ProjectRespDTO> findPublicProjectsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        AppUser artist = this.appUsersService.findByUsername(username);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return this.projectService.findPublicByOwner(artist, pageable);
    }
}