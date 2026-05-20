package solis3d.projectvaultbackend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solis3d.projectvaultbackend.entities.Software;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.NewSoftwareDTO;
import solis3d.projectvaultbackend.repositories.ProjectSoftwareRepository;
import solis3d.projectvaultbackend.repositories.SoftwareRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SoftwareService {

    private final SoftwareRepository softwareRepository;
    private final ProjectSoftwareRepository projectSoftwareRepository;

    public SoftwareService(SoftwareRepository softwareRepository, ProjectSoftwareRepository projectSoftwareRepository) {
        this.softwareRepository = softwareRepository;
        this.projectSoftwareRepository = projectSoftwareRepository;
    }

    public List<Software> findAll() {
        return softwareRepository.findAll();
    }

    public Software findById(UUID softwareId) {
        return  softwareRepository.findById(softwareId).orElseThrow(() -> new NotFoundException("Software con id " + softwareId + " non trovato!"));
    }

    public Software save(NewSoftwareDTO body) {
        if (this.softwareRepository.findByNameIgnoreCase(body.name()).isPresent()) {
            throw new BadRequestException("Software " + body.name() + " già esistente!");
        }

        String slug = this.generateSlug(body.name());

        if(this.softwareRepository.existsBySlug(slug)) {
            throw new BadRequestException("Slug " + slug + " già esistente!");
        }

        Software newSoftware = new Software();
        newSoftware.setName(body.name());
        newSoftware.setSlug(slug);
        newSoftware.setIconUrl(body.iconUrl());
        newSoftware.setCreatedAt(LocalDateTime.now());

        return this.softwareRepository.save(newSoftware);
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    @Transactional
    public Software update(UUID softwareId, NewSoftwareDTO body) {
        Software foundSoftware = this.findById(softwareId);

        if(!foundSoftware.getName().equalsIgnoreCase(body.name()) && this.softwareRepository.findByNameIgnoreCase(body.name()).isPresent()) {
            throw new BadRequestException("Software " +body.name() + " già esistente!");
        }

        String slug = this.generateSlug(body.name());

        if(!foundSoftware.getName().equals(slug) && this.softwareRepository.existsBySlug(slug)) {
            throw new BadRequestException("Slug " + slug + " già esistente!");
        }

        foundSoftware.setName(body.name());
        foundSoftware.setSlug(slug);
        foundSoftware.setIconUrl(body.iconUrl());

        return this.softwareRepository.save(foundSoftware);
    }

    @Transactional
    public void delete(UUID softwareId) {
        Software foundSoftware = this.findById(softwareId);

        if(this.projectSoftwareRepository.existsBySoftware_Id(softwareId)) {
            throw new BadRequestException("Non puoi eliminare un software associato già ad uno o più progetti!");
        }

        this.softwareRepository.delete(foundSoftware);
    }
}
