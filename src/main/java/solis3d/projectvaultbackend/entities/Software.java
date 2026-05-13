package solis3d.projectvaultbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "softwares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Software{

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "icon_url", columnDefinition = "TEXT")
    private String iconUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
