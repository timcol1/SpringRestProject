package avlyakulov.timur.SpringRestProject;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;

@Service
//задача данного класса в том чтобы найти пользователя по имени пользователя в каком то источнике данных и вернуть spring security его
public class ApplicationUserDetailService extends MappingSqlQuery<ApplicationUser> implements UserDetailsService {

    public ApplicationUserDetailService(DataSource ds) {
        super(ds, "select * from application_user where username = :username");
        this.declareParameter(new SqlParameter("username", Types.VARCHAR));
        this.compile();//делает этот объект неизменяемым.
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.executeByNamedParam(Map.of("username", username)).stream().findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find user " + username));
    }

    @Override
    protected ApplicationUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ApplicationUser(rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("password"));
    }
}
