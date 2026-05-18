package solis3d.projectvaultbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.ProjectSoftware;

import java.util.List;
import java.util.UUID;

public interface ProjectSoftwareRepository extends JpaRepository<ProjectSoftware, UUID> {

    List<ProjectSoftware> findByProject_Id(UUID projectId);

    boolean existsByProject_IdAndSoftware_Id(UUID projectId, UUID softwareId);

    void deleteByProject_IdAndSoftware_Id(UUID projectId, UUID softwareId);

    void deleteByProject_Id(UUID projectId);
}
