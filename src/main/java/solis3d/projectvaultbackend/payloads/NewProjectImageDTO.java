package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import solis3d.projectvaultbackend.entities.ProjectImageType;

public record NewProjectImageDTO(
        @NotBlank(message = "L'url dell'immagine è obbligatorio!")
        String imageUrl,

        @Size(max = 150, message = "La caption non può superare i 150 caratteri!")
        String caption,

        @NotNull(message = "Il tipo immagine è obbligatorio!")
        ProjectImageType imageType,

        @Size(max = 50, message = "Lo stage label non può superare i caratteri!")
        String stageLabel,

        @Min(value = 0, message = "L'ordine non può essere negativo!")
        Integer sortOrder
) {
}
