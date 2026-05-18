package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.Size;
import solis3d.projectvaultbackend.entities.ProjectStatus;
import solis3d.projectvaultbackend.entities.ProjectVisibility;

import java.util.List;
import java.util.UUID;

public record UpdateProjectDTO(
        @Size(max = 100, message = "Il titolo del progetto non può superare i 100 caratteri!")
        String title,

        String description,

        String technicalNotes,

        ProjectStatus projectStatus,

        ProjectVisibility projectVisibility,

        String thumbnailUrl,

        String modelUrl,

        UUID categoryId,

        List<UUID> softwareIds

) {
}
