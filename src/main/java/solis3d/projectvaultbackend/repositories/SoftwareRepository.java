package solis3d.projectvaultbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.Software;

import java.util.Optional;
import java.util.UUID;

public interface SoftwareRepository extends JpaRepository<Software, UUID> {

    Optional<Software> findByNameIgnoreCase(String name);

    Optional<Software> findBySlug(String slug);

    boolean existsBySlug(String slug);
    
}
