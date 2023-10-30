package avlyakulov.timur.SpringRestProject;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RequestMapping("api/tasks")
@RestController
public class TasksRestController {
    private final TaskRepository taskRepository;
    private final MessageSource messageSource;

    public TasksRestController(TaskRepository taskRepository,
                               MessageSource messageSource) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
    }


    //@AuthenticationPrincipal - это аннотация, которая используется в Spring Framework, особенно в Spring Security,
    // для получения информации о текущем аутентифицированном пользователе (пользователе, который вошел в систему) в контроллерах или сервисах.
    // Она обеспечивает удобный способ доступа к данным о пользователе, которые были извлечены в процессе аутентификации.
    //
    //Когда @AuthenticationPrincipal используется с аргументами метода в контроллере или сервисе,
    // она автоматически извлекает информацию о текущем аутентифицированном пользователе из контекста безопасности Spring Security
    // и предоставляет ее в виде аргумента метода.
    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks(@AuthenticationPrincipal ApplicationUser user) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.taskRepository.findByApplicationUserId(user.id()));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> handleCreateNewTask(
            @AuthenticationPrincipal ApplicationUser applicationUser,
            @RequestBody NewTaskPayload payload,
            UriComponentsBuilder uriComponentsBuilder,
            Locale locale) {
        if (payload.details() == null || payload.details().isBlank()) {
            final var message = this.messageSource
                    .getMessage("tasks.create.details.errors.not_set",
                            new Object[0], locale);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorsPresentation(
                            List.of(message)));
        } else {
            var task = new Task(payload.details(), applicationUser.id());
            this.taskRepository.save(task);
            return ResponseEntity.created(uriComponentsBuilder
                            .path("/api/tasks/{taskId}")
                            .build(Map.of("taskId", task.id())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(task);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> handleFindTask(@PathVariable("id") UUID id) {
        return ResponseEntity.of(this.taskRepository.findById(id));
    }
}