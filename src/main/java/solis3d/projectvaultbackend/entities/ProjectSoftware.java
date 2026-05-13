package solis3d.projectvaultbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_softwares", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "software_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSoftware {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "software_id", nullable = false)
    private Software software;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    
}
