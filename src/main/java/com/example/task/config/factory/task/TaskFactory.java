package com.example.task.config.factory.task;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.task.category.CategoryRepository;
import com.example.task.category.entities.Category;
import com.example.task.config.BaseFactory;
import com.example.task.config.factory.category.CategoryFactory;
import com.example.task.project.ProjectRepository;
import com.example.task.project.entities.Project;
import com.example.task.task.TaskRepository;
import com.example.task.task.entities.Task;
import com.example.task.task.entities.Urgency;

@Component
@Profile({ "dev", "test" })
public class TaskFactory extends BaseFactory {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryFactory categoryFactory;
    private final ProjectRepository projectRepository;

    public TaskFactory(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            CategoryFactory categoryFactory,
            ProjectRepository projectRepository
    ) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.categoryFactory = categoryFactory;
        this.projectRepository = projectRepository;
    }

    @Override
    public boolean repoEmpty() {
        return taskRepository.count() == 0;
    }

    private Urgency randomUrgency() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 30) {
            return Urgency.LOW;
        }
        if (r < 80) {
            return Urgency.MEDIUM;
        }
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

        if (r < 70) {
            return null;
        }

        int pick = ThreadLocalRandom.current().nextInt(5);

        return switch (pick) {
            case 0 -> 1;
            case 1 -> 7;
            case 2 -> 14;
            case 3 -> 30;
            default -> 0;
        };
    }

    private Project randomProjectOrNull() {
        List<Project> projects = projectRepository.findAll();

        if (projects.isEmpty()) {
            return null;
        }

        int r = ThreadLocalRandom.current().nextInt(100);

        if (r < 25) {
            return null;
        }

        return projects.get(ThreadLocalRandom.current().nextInt(projects.size()));
    }

    public Task create(TaskFactoryOptions options) {
        Task task = new Task();

        String title = (options != null && options.getTitle() != null)
                ? options.getTitle()
                : faker().company().bs();

        Boolean completed = (options != null && options.getCompleted() != null)
                ? options.getCompleted()
                : Boolean.FALSE;

        Boolean archived = (options != null && options.getIsArchived() != null)
                ? options.getIsArchived()
                : Boolean.FALSE;

        Category category = options != null ? options.getCategory() : null;

        if (category == null) {
            List<Category> categories = categoryRepository.findAll();

            if (categories.isEmpty()) {
                category = categoryFactory.create();
            } else {
                category = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));
            }
        }

        Project project = options != null ? options.getProject() : null;

        if (project == null) {
            project = randomProjectOrNull();
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

        task.setTitle(title);
        task.setCompleted(completed);
        task.setArchived(archived);
        task.setCategory(category);
        task.setProject(project);
        task.setDueDate(dueDate);
        task.setUrgency(urgency);
        task.setRecurrenceDays(recurrenceDays);

        return taskRepository.save(task);
    }

    public Task create() {
        return create(new TaskFactoryOptions());
    }

    public void persistAll(List<Task> tasks) {
        taskRepository.saveAllAndFlush(tasks);
    }

    @Override
    public void clear() {
        taskRepository.deleteAll();
    }

    @Override
    public Long findMaxId() {
        return taskRepository.getMaxId();
    }
}