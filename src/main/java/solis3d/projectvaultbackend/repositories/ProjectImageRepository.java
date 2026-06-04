package solis3d.projectvaultbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import solis3d.projectvaultbackend.entities.ProjectImage;
import solis3d.projectvaultbackend.entities.ProjectImageType;
import java.util.List;
import java.util.UUID;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {

    List<ProjectImage> findByProject_IdOrderBySortOrderAsc(UUID projectId);

    List<ProjectImage> findByProject_IdAndImageTypeOrderBySortOrderAsc(UUID projectId, ProjectImageType imageType);

    void deleteByProject_Id(UUID projectId);

}
