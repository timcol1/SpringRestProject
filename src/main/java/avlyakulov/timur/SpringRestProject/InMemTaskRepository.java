package avlyakulov.timur.SpringRestProject;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class InMemTaskRepository implements TaskRepository {
    private final List<Task> tasks = new LinkedList<>();

    @Override
    public List<Task> findAll() {
        return this.tasks;
    }

    @Override
    public void save(Task task) {
        this.tasks.add(task);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return this.tasks.stream()
                .filter(task -> task.id().equals(id))
                .findFirst();
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
