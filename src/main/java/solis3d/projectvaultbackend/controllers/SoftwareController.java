package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.Software;
import solis3d.projectvaultbackend.exceptions.ValidationException;
import solis3d.projectvaultbackend.payloads.NewSoftwareDTO;
import solis3d.projectvaultbackend.services.SoftwareService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/softwares")
public class SoftwareController {

    private final SoftwareService softwareService;

    public SoftwareController(SoftwareService softwareService) {
        this.softwareService = softwareService;
    }

    @GetMapping
    public List<Software> findAll() {
        return softwareService.findAll();
    }

    @GetMapping("/{softwareId}")
    public Software findById(@PathVariable UUID softwareId) {
        return softwareService.findById(softwareId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Software save(@RequestBody @Validated NewSoftwareDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        return softwareService.save(body);
    }

    @PutMapping("/{softwareId}")
    public Software update(@PathVariable UUID softwareId, @RequestBody @Validated NewSoftwareDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        return this.softwareService.update(softwareId, body);
    }

    @DeleteMapping("/{softwareId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID softwareId) {
        this.softwareService.delete(softwareId);
    }

}
