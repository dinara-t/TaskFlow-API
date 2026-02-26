package com.example.todo.config.factory.todo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.todo.category.CategoryRepository;
import com.example.todo.category.entities.Category;
import com.example.todo.config.BaseFactory;
import com.example.todo.config.factory.category.CategoryFactory;
import com.example.todo.todo.TodoRepository;
import com.example.todo.todo.entities.Todo;

@Component
@Profile({ "dev", "test" })
public class TodoFactory extends BaseFactory {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryFactory categoryFactory;

    public TodoFactory(
            TodoRepository todoRepository,
            CategoryRepository categoryRepository,
            CategoryFactory categoryFactory
    ) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
        this.categoryFactory = categoryFactory;
    }

    @Override
    public boolean repoEmpty() {
        return todoRepository.count() == 0;
    }

    public Todo create(TodoFactoryOptions options) {
        Todo todo = new Todo();

        String title = (options != null && options.getTitle() != null)
                ? options.getTitle()
                : faker().company().bs();

        Boolean completed = (options != null && options.getCompleted() != null)
                ? options.getCompleted()
                : Boolean.FALSE;

        Boolean archived = (options != null && options.getIsArchived() != null)
                ? options.getIsArchived()
                : Boolean.FALSE;

        Category category = (options != null) ? options.getCategory() : null;
        if (category == null) {
            List<Category> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                category = categoryFactory.create();
            } else {
                category = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));
            }
        }

        todo.setTitle(title);
        todo.setCompleted(completed);
        todo.setArchived(archived);
        todo.setCategory(category);

        return todoRepository.save(todo);
    }

    public Todo create() {
        return create(new TodoFactoryOptions());
    }

    public void persistAll(List<Todo> todos) {
        todoRepository.saveAllAndFlush(todos);
    }

    @Override
    public void clear() {
        todoRepository.deleteAll();
    }

    @Override
    public Long findMaxId() {
        return todoRepository.getMaxId();
    }
}