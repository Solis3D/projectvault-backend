package solis3d.projectvaultbackend.payloads;

import java.util.UUID;

public record SoftwareRespDTO(
        UUID id,
        String name,
        String slug,
        String iconUrl
) {
}
