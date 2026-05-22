package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank(message = "Il nome è obbligatorio!")
        @Size(min = 2, max = 30, message = "Il nome deve avere tra i 2 e i 30 caratteri!")
        String firstName,

        @NotBlank(message = "Il cognome è obbligatorio!")
        @Size(min = 2, max = 30, message = "Il cognome deve avere tra i 2 e i 30 caratteri!")
        String lastName,

        @NotBlank(message = "Username obbligatorio!")
        @Size(min = 2, max = 30, message = "Lo username deve avere tra i 2 e i 30 caratteri!")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Lo username può contenere solo lettere, numeri, punti, underscore e trattini!"
        )
        String username,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida!")
        @Size(max = 100, message = "L'email non può superare i 100 caratteri!")
        String email,

        @NotBlank(message = "La passoword è obbligatoria")
        @Size(min = 8, message = "La password deve essere di almeno 8 caratteri!")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "La password deve contenere almeno una minuscola, una maiuscola, un numero e un carattere speciale!"
        )
        String password
) {
}
