package solis3d.projectvaultbackend.payloads;

import solis3d.projectvaultbackend.entities.ProjectStatus;
import solis3d.projectvaultbackend.entities.ProjectVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjectRespDTO(
        UUID id,
        String title,
        String description,
        String technicalNotes,
        ProjectStatus projectStatus,
        ProjectVisibility projectVisibility,
        String thumbnailUrl,
        String modelUrl,
        boolean featured,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID ownerId,
        String ownerUsername,
        String ownerAvatarUrl,
        UUID categoryId,
        String categoryName,
        List<SoftwareRespDTO> softwares
) {
}
