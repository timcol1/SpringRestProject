package avlyakulov.timur.SpringRestProject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest {
    // пример модульного теста с библиотекой Mockito.
    // модульные тесты должны быть для всей основной логики приложения которое является ядром.
    @Mock
    TaskRepository taskRepository;

    @Mock
    MessageSource messageSource;

    //создаем мок объект с нашими зависимостями которые мы создали сверху
    @InjectMocks
    TasksRestController controller;//тестируемый объект

    @Test
    @DisplayName("GET api/tasks возвращает HTTP ответ со статусом 200 ОК и списком задач")
//заменяет название метода, на то что мы передали
    void handleGetAllTasks_ReturnsValidResponseEntity() {
        //блок теста можно разделить на 3 блока

        // given
        var tasks = List.of(new Task(UUID.randomUUID(), "Первая задача", false),
                new Task(UUID.randomUUID(), "Вторая задача", true));
        //моделируем поведение mock объекта
        Mockito.doReturn(tasks).when(this.taskRepository).findAll();//мы возвращаем эти задачи, когда у репозитория будет вызван метод findAll().

        // when
        var responseEntity = this.controller.handleGetAllTasks();

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() {
        //given
        var details = "Третья задача";

        //when
        var responseEntity = this.controller.handleCreateNewTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), Locale.UK);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        if (responseEntity.getBody() instanceof Task task) {
            assertNotNull(task.id());
            assertEquals(details, task.details());
            assertFalse(task.completed());

            assertEquals(URI.create("http://localhost:8080/api/tasks/" + task.id()),
                    responseEntity.getHeaders().getLocation());
            //почему здесь мы вызываем verify а в 1 методе нет. В 1 методе мы вызываем операцию чтения,
            //а тут уже мы вызываем запись.
            Mockito.verify(this.taskRepository).save(task);//Mockito проверит, что был вызван этот метод, и был передан объект
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }
        Mockito.verifyNoMoreInteractions(this.taskRepository);//проверяем, чтоб больше никаких не было вызовов в наш репозиторий
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() {
        //given
        var details = "   ";
        var locale = Locale.US;
        var errorMessage = "Details is empty";

        //теперь нужно смоделировать поведение у Mock объекта в нашем случае messageSource и его метод getMessage()
        Mockito.doReturn(errorMessage).when(this.messageSource)
                .getMessage("tasks.create.details.errors.not_set", new Object[0], locale);

        //when - вызов нашего объекта
        //вызов
        var responseEntity = this.controller.handleCreateNewTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        //then - проверка
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of(errorMessage)), responseEntity.getBody());

        //теперь надо убедиться не вызывалось ли у репозитория save, или проверить не вызывались ли методы вообще
        Mockito.verifyNoInteractions(this.taskRepository);
    }

    // TODO: 24.10.2023 Дописать тест для метода контроллера когда он ищет 1 задачу
}