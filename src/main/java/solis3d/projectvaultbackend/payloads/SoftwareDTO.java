package solis3d.projectvaultbackend.payloads;

import java.util.UUID;

public record SoftwareDTO (
        UUID id,
        String name,
        String slug,
        String iconUrl
) {
}
