package softeer.carbook.domain.hashtag.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import softeer.carbook.domain.hashtag.exception.HashtagNotExistException;
import softeer.carbook.domain.hashtag.model.Hashtag;
import softeer.carbook.domain.hashtag.model.Model;
import softeer.carbook.domain.hashtag.model.Type;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HashtagRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HashtagRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Hashtag findHashtagByName(String tag) {
        List<Hashtag> hashtags = jdbcTemplate.query("SELECT h.id, h.tag FROM HASHTAG h WHERE tag = ?", hashtagRowMapper(), tag);
        return hashtags.stream()
                .findAny()
                .orElseThrow(() -> new HashtagNotExistException());
    }

    public List<Type> searchTypeByPrefix(String keyword) {
        return jdbcTemplate.query("SELECT t.id, t.tag FROM TYPE t WHERE tag LIKE '" + keyword + "%'", typeRowMapper());
    }

    public List<Model> searchModelByPrefix(String keyword) {
        return jdbcTemplate.query("SELECT m.id, m.type_id, m.tag FROM MODEL m WHERE tag LIKE '" + keyword + "%'", modelRowMapper());
    }

    public List<Hashtag> searchHashtagByPrefix(String keyword) {
        return jdbcTemplate.query("SELECT h.id, h.tag FROM HASHTAG h WHERE tag LIKE '" + keyword + "%'", hashtagRowMapper());
    }

    private RowMapper<Type> typeRowMapper() {
        return (rs, rowNum) -> new Type(
                rs.getInt("id"),
                rs.getString("tag")
        );
    }

    private RowMapper<Model> modelRowMapper() {
        return (rs, rowNum) -> new Model(
                rs.getInt("id"),
                rs.getInt("type_id"),
                rs.getString("tag")
        );
    }

    private RowMapper<Hashtag> hashtagRowMapper() {
        return (rs, rowNum) -> new Hashtag(
                rs.getInt("id"),
                rs.getString("tag")
        );
    }
}
