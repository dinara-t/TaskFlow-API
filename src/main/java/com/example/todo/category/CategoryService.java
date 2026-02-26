package com.example.todo.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.todo.category.dtos.CategoryResponse;
import com.example.todo.category.dtos.CreateCategoryDto;
import com.example.todo.category.dtos.UpdateCategoryDto;
import com.example.todo.category.entities.Category;
import com.example.todo.common.exception.BadRequestException;
import com.example.todo.common.exception.NotFoundException;
import com.example.todo.common.serviceErrors.NotFoundError;
import com.example.todo.common.serviceErrors.ValidationErrors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public CategoryResponse createCategory(CreateCategoryDto dto) {
        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Name must not be blank");
        }

        if (dto != null && dto.name() != null && categoryRepository.existsByNameIgnoreCase(dto.name().trim())) {
            errors.addError("name", "Category name already exists");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Category category = new Category();
        category.setName(dto.name().trim());

        Category saved = categoryRepository.save(category);

        return new CategoryResponse(saved.getId(), saved.getName());
    }

    public CategoryResponse updateCategory(Long id, UpdateCategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", id)));

        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Name must not be blank");
        }

        if (dto != null && dto.name() != null
                && categoryRepository.existsByNameIgnoreCaseAndIdNot(dto.name().trim(), id)) {
            errors.addError("name", "Category name already exists");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        category.setName(dto.name().trim());
        Category saved = categoryRepository.save(category);

        return new CategoryResponse(saved.getId(), saved.getName());
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", id)));
        categoryRepository.delete(category);
    }
}