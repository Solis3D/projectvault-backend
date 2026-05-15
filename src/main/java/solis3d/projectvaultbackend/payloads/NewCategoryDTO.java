package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCategoryDTO(
        @NotBlank(message = "Il nome della categoria è obbligatorio!")
        @Size(max = 50, message = "Il nome della categoria non può superare i 50 caratteri!")
        String name,

        String description
) {
}
