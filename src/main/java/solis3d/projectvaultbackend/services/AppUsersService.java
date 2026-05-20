package solis3d.projectvaultbackend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.entities.Role;
import solis3d.projectvaultbackend.payloads.CurrentUserDTO;
import solis3d.projectvaultbackend.payloads.UpdateUserDTO;
import solis3d.projectvaultbackend.repositories.AppUserRepository;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.RegisterDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AppUsersService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder bcrypt;
    private final CloudinaryService cloudinaryService;

    public AppUsersService(AppUserRepository appUserRepository, PasswordEncoder bcrypt,  CloudinaryService cloudinaryService) {
        this.appUserRepository = appUserRepository;
        this.bcrypt = bcrypt;
        this.cloudinaryService = cloudinaryService;
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

    @Transactional
    public CurrentUserDTO updateProfile(AppUser currentUser, UpdateUserDTO body) {
        if (body.firstName() != null) {
            currentUser.setFirstName(body.firstName());
        }
        if (body.lastName() != null) {
            currentUser.setLastName(body.lastName());
        }

        if(body.username() != null && !body.username().equals(currentUser.getUsername())) {
            if(this.appUserRepository.existsByUsername(body.username())) {
                throw new BadRequestException("Username " + body.username() + " già in uso!");
            }

            currentUser.setUsername(body.username());
        }

        if (body.avatarUrl() != null) {
            currentUser.setAvatarUrl(body.avatarUrl());
        }

        if (body.position() != null) {
            currentUser.setPosition(body.position());
        }

        if (body.bio() != null) {
            currentUser.setBio(body.bio());
        }

        currentUser.setUpdatedAt(LocalDateTime.now());

        return this.mapToDTO(this.appUserRepository.save(currentUser));
    }

    @Transactional
    public CurrentUserDTO uploadAvatar(AppUser currentUser, MultipartFile file) {
        String avatarUrl = this.cloudinaryService.uploadImage(file);

        currentUser.setAvatarUrl(avatarUrl);
        currentUser.setUpdatedAt(LocalDateTime.now());

        return this.mapToDTO(this.appUserRepository.save(currentUser));
    }

    public CurrentUserDTO mapToDTO(AppUser appUser) {
        return new CurrentUserDTO(
                appUser.getId(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getUsername(),
                appUser.getEmail(),
                appUser.getRole(),
                appUser.getAvatarUrl(),
                appUser.getPosition(),
                appUser.getBio()
        );
    }
}