package solis3d.projectvaultbackend.payloads;

import solis3d.projectvaultbackend.entities.ProjectImageType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectImageRespDTO(
        UUID id,
        String imageUrl,
        String caption,
        ProjectImageType imageType,
        String stageLabel,
        Integer sortOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID projectId
) {
}
