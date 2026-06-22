package com.example.task.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.task.category.dtos.CategoryResponse;
import com.example.task.category.dtos.CreateCategoryDto;
import com.example.task.category.dtos.UpdateCategoryDto;
import com.example.task.category.entities.Category;
import com.example.task.common.dto.PageResponse;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;

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

    public PageResponse<CategoryResponse> getCategoriesPaged(Integer page, Integer size) {
        int safePage = page == null ? 0 : page;
        int safeSize = size == null ? 50 : size;

        ValidationErrors errors = new ValidationErrors();
        if (safePage < 0) {
            errors.addError("page", "Page must be 0 or greater");
        }
        if (safeSize <= 0) {
            errors.addError("size", "Size must be greater than 0");
        }
        if (safeSize > 200) {
            errors.addError("size", "Size must be 200 or less");
        }
        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        PageRequest pageable = PageRequest.of(safePage, safeSize);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponse> items = categoryPage.getContent().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();

        return new PageResponse<>(
                items,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.hasNext(),
                categoryPage.hasPrevious()
        );
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