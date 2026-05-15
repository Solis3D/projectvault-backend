package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewSoftwareDTO(
        @NotBlank(message = "il nome del software è obbligatorio!")
        @Size(max = 50, message = "il nome del software non può superare i 50 caratteri!")
        String name,

        String iconUrl
) {
}