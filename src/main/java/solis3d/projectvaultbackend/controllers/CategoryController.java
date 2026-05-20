package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.Category;
import solis3d.projectvaultbackend.exceptions.ValidationException;
import solis3d.projectvaultbackend.payloads.NewCategoryDTO;
import solis3d.projectvaultbackend.services.CategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> findAll() {
        return this.categoryService.findAll();
    }

    @GetMapping("/{categoryId}")
    public Category findById(@PathVariable UUID categoryId) {
        return this.categoryService.findById(categoryId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category save(@RequestBody @Validated NewCategoryDTO body, BindingResult validationResult){
        if(validationResult.hasErrors()){
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        return this.categoryService.save(body);
    }

    @PutMapping("{categoryId}")
    public Category update(@PathVariable UUID categoryId, @RequestBody @Validated NewCategoryDTO body, BindingResult validationResult) {
        if(validationResult.hasErrors()){
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        return this.categoryService.update(categoryId, body);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID categoryId) {
        this.categoryService.delete(categoryId);
    }
}
