package solis3d.projectvaultbackend.payloads;

import solis3d.projectvaultbackend.entities.ProjectImageType;

public record UpdateProjectImageDTO(
        String imageUrl,
        String caption,
        ProjectImageType imageType,
        String stageLabel,
        Integer sortOrder
) {
}
