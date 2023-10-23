package avlyakulov.timur.SpringRestProject;

import java.util.List;

public interface TaskRepository {

    List<Task> findAll();
}