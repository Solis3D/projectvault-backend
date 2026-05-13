package solis3d.projectvaultbackend.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.ProjectSoftware;

import java.util.List;
import java.util.UUID;

public interface ProjectImageRepository extends JpaRepository<ProjectSoftware, UUID> {

    List<ProjectSoftware> findByProjectId(UUID projectId);

    boolean existsByProjectIdAndSoftwareId(UUID projectId, UUID softwareId);

    void deleteByProjectIdAndSoftwareId(UUID projectId, UUID softwareId);

}
