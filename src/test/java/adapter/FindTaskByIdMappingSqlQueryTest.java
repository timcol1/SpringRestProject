package adapter;

import avlyakulov.timur.practise.adapter.FindTaskByIdMappingSqlQuery;
import avlyakulov.timur.practise.adapter.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FindTaskByIdMappingSqlQueryTest {

    @Spy
    FindTaskByIdMappingSqlQuery adapter;

    @Test
    void findTaskById_CallsFindObjectByNamedParam_ReturnsOptional() {
        //given
        var id = UUID.randomUUID();
        var task = new Task(id);

        Mockito.doReturn(task).when(this.adapter).findObjectByNamedParam(Map.of("id", id));

        //when
        var optional = this.adapter.findTaskById(id);

        //then
        assertEquals(Optional.of(task), optional);
    }
}
