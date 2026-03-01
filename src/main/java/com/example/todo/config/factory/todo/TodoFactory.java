package com.example.todo.config.factory.todo;

import java.time.LocalDate;
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
import com.example.todo.todo.entities.Urgency;

@Component
@Profile({ "dev", "test" })
public class TodoFactory extends BaseFactory {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryFactory categoryFactory;

    public TodoFactory(
            TodoRepository todoRepository,
            CategoryRepository categoryRepository,
            CategoryFactory categoryFactory) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
        this.categoryFactory = categoryFactory;
    }

    @Override
    public boolean repoEmpty() {
        return todoRepository.count() == 0;
    }

    private Urgency randomUrgency() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 30) return Urgency.LOW;
        if (r < 80) return Urgency.MEDIUM;
        return Urgency.HIGH;
    }

    private LocalDate randomDueDate() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 45) {
            return LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(1, 45));
        }
        if (r < 60) {
            return LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(1, 20));
        }
        return null;
    }

    private Integer randomRecurrenceDays() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 70) return null;
        int pick = ThreadLocalRandom.current().nextInt(5);
        return switch (pick) {
            case 0 -> 1;
            case 1 -> 7;
            case 2 -> 14;
            case 3 -> 30;
            default -> 0;
        };
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

        LocalDate dueDate = (options != null && options.getDueDate() != null)
                ? options.getDueDate()
                : randomDueDate();

        Urgency urgency = (options != null && options.getUrgency() != null)
                ? options.getUrgency()
                : randomUrgency();

        Integer recurrenceDays = (options != null && options.getRecurrenceDays() != null)
                ? options.getRecurrenceDays()
                : randomRecurrenceDays();

        if (recurrenceDays != null && recurrenceDays == 0) {
            recurrenceDays = null;
        }

        todo.setTitle(title);
        todo.setCompleted(completed);
        todo.setArchived(archived);
        todo.setCategory(category);
        todo.setDueDate(dueDate);
        todo.setUrgency(urgency);
        todo.setRecurrenceDays(recurrenceDays);

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