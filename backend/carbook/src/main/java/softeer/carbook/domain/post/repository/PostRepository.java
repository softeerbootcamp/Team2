package softeer.carbook.domain.post.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import softeer.carbook.domain.post.model.Post;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* deprecated
    public List<Post> getPosts(int size, int index) {
        return jdbcTemplate.query("SELECT * from POST ORDER BY create_date DESC LIMIT ?, ?",
                postRowMapper(), index, size);
    }

    public List<Post> getPostsByUserId(int size, int index, int userId){
        List<Post> posts = jdbcTemplate.query("select * from POST where user_id = ? ORDER BY create_date DESC LIMIT ?, ?",
                postRowMapper(), userId, index, size);
        return posts;
    }

    public List<Post> getFollowingPosts(int size, int index, int followerId){
        List<Post> posts = jdbcTemplate.query("select * from POST where user_id in (select following from FOLLOW where follower = ?) ORDER BY create_date DESC LIMIT ?, ?",
                postRowMapper(), followerId, index, size);
        return posts;
    }

     */
    public Post findPostById(int postId) {
        List<Post> post = jdbcTemplate.query(
                "select id, user_id, create_date, update_date, content, model_id " +
                        "from POST " +
                        "where id = ?", postRowMapper(), postId);
        return post.stream().findAny().orElseThrow();
    }

    public List<Post> searchByType(String type) {
        return jdbcTemplate.query("SELECT p.id, p.user_id, p.create_date, p.update_date, p.content, p.model_id FROM TYPE t " +
                "INNER JOIN MODEL m ON (t.id = m.type_id AND t.tag = ?) " +
                "INNER JOIN POST p ON m.id = p.model_id ORDER BY p.create_date DESC", postRowMapper(), type);
    }

    public List<Post> searchByModel(String model) {
        return jdbcTemplate.query("SELECT p.id, p.user_id, p.create_date, p.update_date, p.content, p.model_id FROM MODEL m " +
                "INNER JOIN POST p ON (m.id = p.model_id AND m.tag = ?) ORDER BY p.create_date DESC", postRowMapper(), model);
    }

    public List<Post> searchByHashtag(String tagName) {
        return jdbcTemplate.query("SELECT p.id, p.user_id, p.create_date, p.update_date, p.content, p.model_id FROM POST p " +
                "INNER JOIN POST_HASHTAG ph ON p.id = ph.post_id " +
                "INNER JOIN HASHTAG h ON ph.tag_id = h.id AND h.tag = ? ORDER BY p.create_date DESC", postRowMapper(), tagName);
    }

    public int addPost(Post post){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO POST (user_id, content, model_id) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setInt(3, post.getModelId());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void updatePost(Post post) {
        jdbcTemplate.update("update POST set update_date=?, content=?, model_id=? where id=?",
                post.getUpdateDate(), post.getContent(), post.getModelId(), post.getId());
    }

    private RowMapper<Post> postRowMapper() {
        return (rs, rowNum) -> new Post(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getTimestamp("create_date"),
                rs.getTimestamp("update_date"),
                rs.getString("content"),
                rs.getInt("model_id")
        );
    }
}
