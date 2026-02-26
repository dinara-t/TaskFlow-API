package com.example.todo.config.factory.category;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.todo.category.CategoryRepository;
import com.example.todo.category.entities.Category;
import com.example.todo.config.BaseFactory;

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
                ? options.getName()
                : faker().commerce().department();

        category.setName(name);

        return categoryRepository.save(category);
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