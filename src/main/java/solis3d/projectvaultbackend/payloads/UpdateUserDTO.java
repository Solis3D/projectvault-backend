package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Size(max = 50, message = "Il nome non può superare i 50 caratteri!")
        String firstName,

        @Size(max = 50, message = "Il cognome non può superare i 50 caratteri!")
        String lastName,

        @Size(max = 30, message = "L'username non può superare i 30 caratteri!")
        String username,

        String avatarUrl,

        @Size(max = 150, message = "La posizione non può superare i 150 caratteri!")
        String position,

        String bio
) {
}
