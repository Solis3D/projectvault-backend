package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import solis3d.projectvaultbackend.entities.ProjectStatus;
import solis3d.projectvaultbackend.entities.ProjectVisibility;

import java.util.List;
import java.util.UUID;

public record NewProjectDTO(
        @NotBlank(message = "Il titolo del progetto è obbligatorio!")
        @Size(max = 100, message = "Il titolo del progetto non può superare i 100 caratteri!")
        String title,

        @NotBlank (message = "La descrizione del progetto è obbligatoria!")
        @Size(max = 3000, message = "La descrizione non può superare i 3000 caratteri!")
        String description,

        @Size(max = 3000, message = "Le note tecniche non possono superare i 3000 caratteri!")
        String technicalNotes,

        @NotNull(message = "Lo stato del progetto è obbligatorio!")
        ProjectStatus projectStatus,

        @NotNull(message = "La visibilità del progetto è obbligatoria!")
        ProjectVisibility projectVisibility,

        String thumbnailUrl,

        String modelUrl,

        @NotNull (message = "La categoria è obbligatoria!")
        UUID categoryId,

        @Size(max = 20, message = "Non puoi associare più di 20 software a un progetto!")
        List<UUID> softwareIds
) {
}
