package avlyakulov.timur.practise.adapter;

import org.springframework.jdbc.object.MappingSqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FindTaskByIdMappingSqlQuery extends MappingSqlQuery<Task>
        implements FindTaskById {

    @Override
    public Optional<Task> findTaskById(UUID id) {
        return Optional.ofNullable(this.findObjectByNamedParam(Map.of("id", id)));
    }

    @Override
    protected Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Task(rs.getObject("id", UUID.class));
    }
}
