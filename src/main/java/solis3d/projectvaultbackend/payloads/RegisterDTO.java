package solis3d.projectvaultbackend.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        String username,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida!")
        String email,

        @NotBlank(message = "La passoword è obbligatoria")
        @Size(min = 6, message = "La password deve essere di almeno 6 caratteri!")
        String password
) {
}
