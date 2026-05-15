package solis3d.projectvaultbackend.services;

import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.Category;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.NewCategoryDTO;
import solis3d.projectvaultbackend.repositories.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    public Category findById(UUID categoryId) {
        return this.categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Categoria con id " + categoryId + " non trovata!"));
    }

    public Category save(NewCategoryDTO body) {
        if(this.categoryRepository.existsByName(body.name())) {
            throw new BadRequestException("Categoria " + body.name() + " già esistente!");
        }

        Category newCategory = new Category();
        newCategory.setName(body.name());
        newCategory.setDescription(body.description());

        return categoryRepository.save(newCategory);
    }
}
