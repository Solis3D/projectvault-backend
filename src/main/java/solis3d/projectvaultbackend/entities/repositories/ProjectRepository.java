package solis3d.projectvaultbackend.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.Project;
import solis3d.projectvaultbackend.entities.ProjectVisibility;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByOwnerId(UUID ownerId);

    List<Project> findByVisibility(ProjectVisibility visibility);

    List<Project> findByCategoryId(UUID categoryId);

    List<Project> findByTitleContainingIgnoreCase(String title);

}
