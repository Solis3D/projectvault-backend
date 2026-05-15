package solis3d.projectvaultbackend.services;

import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.Software;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.NewSoftwareDTO;
import solis3d.projectvaultbackend.repositories.SoftwareRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SoftwareService {

    private final SoftwareRepository softwareRepository;

    public SoftwareService(SoftwareRepository softwareRepository) {
        this.softwareRepository = softwareRepository;
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
}
