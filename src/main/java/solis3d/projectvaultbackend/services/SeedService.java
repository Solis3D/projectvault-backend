package solis3d.projectvaultbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.entities.Category;
import solis3d.projectvaultbackend.entities.Role;
import solis3d.projectvaultbackend.entities.Software;
import solis3d.projectvaultbackend.repositories.AppUserRepository;
import solis3d.projectvaultbackend.repositories.CategoryRepository;
import solis3d.projectvaultbackend.repositories.SoftwareRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeedService implements CommandLineRunner{

    private final CategoryRepository categoryRepository;
    private final SoftwareRepository softwareRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder bcrypt;

    @Value("${admin.first-name}")
    private String adminFirstName;

    @Value("${admin.last-name}")
    private String adminLastName;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public SeedService(CategoryRepository categoryRepository, SoftwareRepository softwareRepository, AppUserRepository appUserRepository, PasswordEncoder bcrypt) {
        this.categoryRepository = categoryRepository;
        this.softwareRepository = softwareRepository;
        this.appUserRepository = appUserRepository;
        this.bcrypt = bcrypt;
    }

    @Override
    public void run(String... args) {
        this.seedCategories();
        this.seedSoftwares();
        this.seedAdminUser();
    }

    private void seedCategories() {
        List<String> categories = List.of(
                "Environment",
                "Prop",
                "Character",
                "Vehicle",
                "Weapon",
                "Material",
                "Concept Art",
                "Other"
        );

        categories.forEach(categoryName -> {
            if (!this.categoryRepository.existsByName(categoryName)) {
                Category category = new Category();
                category.setName(categoryName);
                category.setDescription(null);

                this.categoryRepository.save(category);
            }
        });
    }

    private void seedSoftwares() {
        List<String> softwares = List.of(
                "Maya",
                "Blender",
                "3DS Max",
                "Substance Painter",
                "Substance Designer",
                "Substance 3D Sampler",
                "Unreal Engine",
                "Unity",
                "ZBrush",
                "Marmoset Toolbag",
                "Photoshop",
                "RizomUV",
                "Houdini",
                "Marvelous Designer",
                "Gaea",
                "Embergen",
                "SpeedTree",
                "TopoGun",
                "Affinity"

        );

        softwares.forEach(softwareName -> {
            String slug = this.generateSlug(softwareName);

            if (!this.softwareRepository.existsBySlug(slug)) {
                Software software = new Software();
                software.setName(softwareName);
                software.setSlug(slug);
                software.setIconUrl(null);
                software.setCreatedAt(LocalDateTime.now());

                this.softwareRepository.save(software);
            }
        });
    }

    private void seedAdminUser() {
        if (!this.appUserRepository.existsByEmail(adminEmail) && !this.appUserRepository.existsByUsername(adminUsername)) {
            AppUser admin = new AppUser();
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(this.bcrypt.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setCreatedAt(LocalDateTime.now());

            this.appUserRepository.save(admin);
        }
    }


    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
