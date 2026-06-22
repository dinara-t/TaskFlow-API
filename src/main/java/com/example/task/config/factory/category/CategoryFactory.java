package com.example.task.config.factory.category;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.task.category.CategoryRepository;
import com.example.task.category.entities.Category;
import com.example.task.config.BaseFactory;

@Component
@Profile({ "dev", "test" })
public class CategoryFactory extends BaseFactory {

    private final CategoryRepository categoryRepository;

    public CategoryFactory(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean repoEmpty() {
        return categoryRepository.count() == 0;
    }

    public Category create(CategoryFactoryOptions options) {
        Category category = new Category();

        String name = (options != null && options.getName() != null)
                ? options.getName().trim()
                : uniqueCategoryName();

        category.setName(name);

        return categoryRepository.save(category);
    }

    private String uniqueCategoryName() {
        String name;
        int attempts = 0;

        do {
            name = faker().commerce().department().trim();
            attempts++;
        } while (categoryRepository.existsByNameIgnoreCase(name) && attempts < 50);

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            name = faker().commerce().department().trim() + " " + incrementAndGet();
        }

        return name;
    }

    public Category create() {
        return create(new CategoryFactoryOptions());
    }

    public void persistAll(List<Category> categories) {
        categoryRepository.saveAllAndFlush(categories);
    }

    @Override
    public void clear() {
        categoryRepository.deleteAll();
    }

    @Override
    public Long findMaxId() {
        return categoryRepository.getMaxId();
    }
}