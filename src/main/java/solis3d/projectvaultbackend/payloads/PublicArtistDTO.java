package solis3d.projectvaultbackend.payloads;

import java.util.UUID;

public record PublicArtistDTO(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String avatarUrl,
        String position,
        String bio
) {
}
