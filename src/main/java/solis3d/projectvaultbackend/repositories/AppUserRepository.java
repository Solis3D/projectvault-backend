package solis3d.projectvaultbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.entities.ProjectVisibility;
import solis3d.projectvaultbackend.entities.Role;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
            SELECT appUser
            FROM AppUser appUser
            WHERE appUser.role = :role
            AND EXISTS (
                SELECT project.id
                FROM Project project
                WHERE project.owner = appUser
                AND project.projectVisibility = :visibility
            )
            AND (
                LOWER(appUser.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(appUser.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(appUser.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(CONCAT(appUser.firstName, CONCAT(' ', appUser.lastName)))
                    LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """)
    Page<AppUser> searchPublicArtists(
            @Param("query") String query,
            @Param("role") Role role,
            @Param("visibility") ProjectVisibility visibility,
            Pageable pageable
    );
}
