package solis3d.projectvaultbackend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solis3d.projectvaultbackend.entities.Category;
import solis3d.projectvaultbackend.exceptions.BadRequestException;
import solis3d.projectvaultbackend.exceptions.NotFoundException;
import solis3d.projectvaultbackend.payloads.NewCategoryDTO;
import solis3d.projectvaultbackend.repositories.CategoryRepository;
import solis3d.projectvaultbackend.repositories.ProjectRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;

    public CategoryService(CategoryRepository categoryRepository, ProjectRepository projectRepository) {
        this.categoryRepository = categoryRepository;
        this.projectRepository = projectRepository;
    }

    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    public Category findById(UUID categoryId) {
        return this.categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Categoria con id " + categoryId + " non trovata!"));
    }

    @Transactional
    public Category save(NewCategoryDTO body) {
        if(this.categoryRepository.existsByName(body.name())) {
            throw new BadRequestException("Categoria " + body.name() + " già esistente!");
        }

        Category newCategory = new Category();
        newCategory.setName(body.name());
        newCategory.setDescription(body.description());

        return categoryRepository.save(newCategory);
    }

    @Transactional
    public Category update(UUID categoryId, NewCategoryDTO body) {
        Category foundCategory = this.findById(categoryId);

        if(!foundCategory.getName().equals(body.name()) && this.categoryRepository.existsByName(body.name())) {
            throw new BadRequestException("Categoria " + body.name() + " già esistente!");
        }

        foundCategory.setName(body.name());
        foundCategory.setDescription(body.description());

        return this.categoryRepository.save(foundCategory);
    }

    @Transactional
    public void delete(UUID categoryId) {
        Category foundCategory = this.findById(categoryId);

        if(this.projectRepository.existsByCategory_Id(categoryId)) {
            throw new BadRequestException("Non puoi eliminare una categoria associata già a uno o più progetti!");
        }

        this.categoryRepository.delete(foundCategory);

    }
}
