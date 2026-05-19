package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import solis3d.projectvaultbackend.entities.ProjectImageType;

public record NewProjectImageDTO(
        @NotBlank(message = "L'url dell'immagine è obbligatorio!")
        String imageUrl,

        String caption,

        @NotNull(message = "Il tipo immagine è obbligatorio!")
        ProjectImageType imageType,

        String stageLabel,

        Integer sortOrder
) {
}
