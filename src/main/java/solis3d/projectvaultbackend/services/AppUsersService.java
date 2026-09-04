package solis3d.projectvaultbackend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import solis3d.projectvaultbackend.payloads.CloudinaryUploadRespDTO;
import solis3d.projectvaultbackend.payloads.RegisterDTO;
import solis3d.projectvaultbackend.payloads.PublicArtistDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AppUsersService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder bcrypt;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;

    public AppUsersService(AppUserRepository appUserRepository, PasswordEncoder bcrypt,  CloudinaryService cloudinaryService, EmailService emailService) {
        this.appUserRepository = appUserRepository;
        this.bcrypt = bcrypt;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
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

        AppUser savedUser = appUserRepository.save(newUser);
        this.emailService.sendWelcomeEmail(savedUser);

        return savedUser;
    }

    public AppUser findById(UUID userId) {
        return this.appUserRepository.findById(userId).orElseThrow(() -> new NotFoundException("Utente con id " + userId + " non trovato!"));
    }

    public AppUser findByEmail(String email) {
        return this.appUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
    }

    public AppUser findByUsername(String username) {
        return this.appUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new NotFoundException("Utente con username " + username + " non trovato!"));
    }

    public PublicArtistDTO findPublicByUsername(String username) {
        AppUser appUser = this.findByUsername(username);
        return this.mapToPublicDTO(appUser);
    }

    public Page<CurrentUserDTO> findAll(Pageable pageable) {
        return this.appUserRepository.findAll(pageable)
                .map(this::mapToDTO);
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
        CloudinaryUploadRespDTO uploadedAvatar = this.cloudinaryService.uploadImage(file);

        if(currentUser.getAvatarPublicId() != null) {
            this.cloudinaryService.deleteImage(currentUser.getAvatarPublicId());
        } else {
            this.cloudinaryService.deleteImageByUrl(currentUser.getAvatarUrl());
        }

        currentUser.setAvatarUrl(uploadedAvatar.imageUrl());
        currentUser.setAvatarPublicId(uploadedAvatar.publicId());
        currentUser.setUpdatedAt(LocalDateTime.now());

        return this.mapToDTO(this.appUserRepository.save(currentUser));
    }

    public long countAll() {
        return this.appUserRepository.count();
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

    public PublicArtistDTO mapToPublicDTO(AppUser appUser) {
        return new PublicArtistDTO(
                appUser.getId(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getUsername(),
                appUser.getAvatarUrl(),
                appUser.getPosition(),
                appUser.getBio()
        );
    }
}
