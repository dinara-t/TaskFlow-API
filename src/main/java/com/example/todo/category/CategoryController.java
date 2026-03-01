package com.example.todo.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.todo.category.dtos.CategoryResponse;
import com.example.todo.category.dtos.CreateCategoryDto;
import com.example.todo.category.dtos.UpdateCategoryDto;
import com.example.todo.common.dto.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/paged")
    public PageResponse<CategoryResponse> getCategoriesPaged(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return categoryService.getCategoriesPaged(page, size);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryDto dto) {
        CategoryResponse created = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryDto dto) {
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}