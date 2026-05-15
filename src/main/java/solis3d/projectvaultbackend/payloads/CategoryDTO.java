package solis3d.projectvaultbackend.payloads;

import java.util.UUID;

public record CategoryDTO(
        UUID id,
        String name,
        String description
) {
}
