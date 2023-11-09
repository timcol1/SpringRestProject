package avlyakulov.timur.practise.adapter;

import java.util.Optional;
import java.util.UUID;

public interface FindTaskById {

    Optional<Task> findTaskById(UUID id);
}
