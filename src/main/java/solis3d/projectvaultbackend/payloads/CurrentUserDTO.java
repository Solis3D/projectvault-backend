package solis3d.projectvaultbackend.payloads;

import solis3d.projectvaultbackend.entities.Role;

import java.util.UUID;

public record CurrentUserDTO(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        Role role,
        String avatarUrl,
        String position,
        String bio
) {
}
