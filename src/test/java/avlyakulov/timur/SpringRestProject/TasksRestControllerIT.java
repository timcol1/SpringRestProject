package avlyakulov.timur.SpringRestProject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

@SpringBootTest//для этого теста нам нужно разворачивать наше приложение, проверять его уже во время работы
//здесь мы тестируем RestEndPoint.
@AutoConfigureMockMvc(printOnlyOnFailure = false)//это делается для того чтобы мы в логах могли посмотреть на запросы и ответы
class TasksRestControllerIT {
    //сокращение IT значит integration test
    //интеграционные тесты должны покрывать все
    //интеграционные тесты, сильно зависят от внешних факторов. Они получаются хрупкими.
    //Нужно постараться чтоб каждый тест был изолирован. Тесты не должны опираться на выполнение друг друга.

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemTaskRepository taskRepository;//когда мы перенесем наши данные в бд, то уже придется переписать

    //теперь нам нужно очищать наш репозиторий от локальных значений, чтоб в других тестах не опираться на эти значения.
    @AfterEach
    void tearDown() {
        this.taskRepository.getTasks().clear();
    }


    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/tasks");
        this.taskRepository.getTasks().addAll(List.of(new Task(UUID.fromString("f69d7733-558d-46e3-90e1-e20ab7a3dd44"),
                        "Первая задача", false),
                new Task(UUID.fromString("276340f3-bd41-45ab-adc6-2254f9650517"),
                        "Вторая задача", true)));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                //теперь работаем с результатом
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                [
                                    {
                                        "id" : "f69d7733-558d-46e3-90e1-e20ab7a3dd44",
                                        "details" : "Первая задача",
                                        "completed" : false
                                    },
                                    {
                                        "id" : "276340f3-bd41-45ab-adc6-2254f9650517",
                                        "details" : "Вторая задача",
                                        "completed" : true
                                    }
                                ]                                                    
                                """)
                );
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details" : "Третья задача"
                        }
                        """);

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                {
                                    "details" : "Третья задача",
                                    "completed" : false
                                }
                                """),
                        //мы не задали идентификатор та как он генерится автоматически, но мы проверим его наличие
                        MockMvcResultMatchers.jsonPath("$.id").exists()
                );
        //теперь нужно проверить что в репозитории только 1 задача, и она соответствует тому что мы туда передали
        Assertions.assertEquals(1, this.taskRepository.getTasks().size());
        final var task = this.taskRepository.getTasks().get(0);
        Assertions.assertNotNull(task.id());
        Assertions.assertEquals("Третья задача", task.details());
        Assertions.assertFalse(task.completed());
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/api/tasks")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details" : null
                        }
                        """);

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.header().doesNotExist(HttpHeaders.LOCATION),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                {
                                    "errors" : ["Task details must be set"]
                                }
                                """, true)//задаем строгость, она валидируется строго
                );
        //теперь нужно проверить что в репозитории не появилось никаких задач
        Assertions.assertTrue(this.taskRepository.getTasks().isEmpty());
    }
}