package softeer.carbook.domain.user.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import softeer.carbook.domain.user.dto.SignupForm;
import softeer.carbook.domain.user.model.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUser(SignupForm signupForm) {
        jdbcTemplate.update("insert into user values(?, ?, ?)",
                signupForm.getEmail(),
                signupForm.getNickname(),
                signupForm.getPassword());
    }

    public boolean isEmailDuplicated(String email) {
        List<User> result = jdbcTemplate.query("select * from user where email = ?", userRowMapper(), email);
        return !result.isEmpty();
    }

    public boolean isNicknameDuplicated(String nickname) {
        List<User> result = jdbcTemplate.query("select * from user where nickname = ?", userRowMapper(), nickname);
        return !result.isEmpty();
    }


    private RowMapper<User> userRowMapper(){
        return (rs, rowNum) -> {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("nickname"),
                    rs.getString("password")
            );
            return user;
        };
    }


}
