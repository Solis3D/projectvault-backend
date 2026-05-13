package solis3d.projectvaultbackend.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.Project;
import solis3d.projectvaultbackend.entities.ProjectVisibility;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByOwner_Id(UUID ownerId);

    List<Project> findByProjectVisibility(ProjectVisibility projectVisibility);

    List<Project> findByCategory_Id(UUID categoryId);

    List<Project> findByTitleContainingIgnoreCase(String title);

}
