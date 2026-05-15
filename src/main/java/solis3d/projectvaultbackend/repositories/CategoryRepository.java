package solis3d.projectvaultbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
    
}
