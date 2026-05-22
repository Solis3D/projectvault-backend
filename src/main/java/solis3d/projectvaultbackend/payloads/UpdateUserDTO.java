package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Size(max = 50, message = "Il nome non può superare i 50 caratteri!")
        String firstName,

        @Size(max = 50, message = "Il cognome non può superare i 50 caratteri!")
        String lastName,

        @Size(min = 2, max = 30, message = "L'username deve essere compreso tra i 2 e i 30 caratteri!")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Lo username può contenere solo lettere, numeri, punti, underscore e trattini!"
        )
        String username,

        String avatarUrl,

        @Size(max = 150, message = "La posizione non può superare i 150 caratteri!")
        String position,

        @Size(max = 1000, message = "La bio non può superare i 1000 caratteri!")
        String bio
) {
}
