package solis3d.projectvaultbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "technical_notes", columnDefinition = "TEXT")
    private String technicalNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "pipeline_status", nullable = false)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectVisibility projectVisibility;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "youtube_url", columnDefinition = "TEXT")
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "viewer_type")
    private ProjectViewerType viewerType;

    @Column(name = "model_url", columnDefinition = "TEXT")
    private String modelUrl;

    @Column(nullable = false)
    private boolean featured;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
