package solis3d.projectvaultbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.Project;
import solis3d.projectvaultbackend.entities.ProjectVisibility;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByOwner_Id(UUID ownerId, Pageable pageable);

    Page<Project> findByProjectVisibility(ProjectVisibility projectVisibility, Pageable pageable);

    List<Project> findByCategory_Id(UUID categoryId);

    List<Project> findByTitleContainingIgnoreCase(String title);

    Page<Project> findByProjectVisibilityAndTitleContainingIgnoreCase(ProjectVisibility projectVisibility, String title, Pageable pageable);

    Page<Project> findByProjectVisibilityAndCategory_Id(ProjectVisibility projectVisibility, UUID categoryId, Pageable pageable);

    Page<Project> findByProjectVisibilityAndTitleContainingIgnoreCaseAndCategory_Id(ProjectVisibility projectVisibility, String title, UUID categoryId, Pageable pageable);

    long countByProjectVisibility(ProjectVisibility projectVisibility);

    boolean existsByCategory_Id(UUID categoryId);
}
