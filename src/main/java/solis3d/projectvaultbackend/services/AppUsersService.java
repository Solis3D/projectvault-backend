package solis3d.projectvaultbackend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.entities.Role;
import solis3d.projectvaultbackend.entities.repositories.AppUserRepository;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.RegisterDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AppUsersService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder bcrypt;

    public AppUsersService(AppUserRepository appUserRepository, PasswordEncoder bcrypt) {
        this.appUserRepository = appUserRepository;
        this.bcrypt = bcrypt;
    }

    public AppUser saveNewUser(RegisterDTO body) {
        if (this.appUserRepository.existsByEmail(body.email())) {
            throw new BadRequestException("Email" + body.email() + "già in uso!");
        }

        if (this.appUserRepository.existsByUsername(body.username())) {
            throw new BadRequestException("Username" + body.username() + "già in uso!");
        }

        AppUser newUser = new AppUser();
        newUser.setFirstName(body.firstName());
        newUser.setLastName(body.lastName());
        newUser.setUsername(body.username());
        newUser.setEmail(body.email());
        newUser.setPassword(bcrypt.encode(body.password()));
        newUser.setRole(Role.USER);
        newUser.setCreatedAt(LocalDateTime.now());

        return this.appUserRepository.save(newUser);
    }

    public AppUser findById(UUID userId) {
        return this.appUserRepository.findById(userId).orElseThrow(() -> new NotFoundException("Utente con id " + userId + " non trovato!"));
    }

    public AppUser findByEmail(String email) {
        return this.appUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
    }
}
