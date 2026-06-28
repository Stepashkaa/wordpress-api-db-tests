package ru.simbirsoft.db;

import java.sql.*;
import java.time.LocalDateTime;

import static ru.simbirsoft.db.DbDataHelper.*;

public class PostDataRepository {

    private static final int DEFAULT_PARENT_ID = 0;
    private static final int DEFAULT_MENU_ORDER = 0;
    private static final int DEFAULT_COMMENT_COUNT = 0;
    private static final String EMPTY_VALUE = "";

    public int createPublishedPostEntity(String title, String content) {
        String sql = """
                INSERT INTO wp_posts (post_author,
                                      post_date,
                                      post_date_gmt,
                                      post_content,
                                      post_title,
                                      post_excerpt,
                                      post_status,
                                      comment_status,
                                      ping_status,
                                      post_password,
                                      post_name,
                                      to_ping,
                                      pinged,
                                      post_modified,
                                      post_modified_gmt,
                                      post_content_filtered,
                                      post_parent,
                                      guid,
                                      menu_order,
                                      post_type,
                                      post_mime_type,
                                      comment_count
                                  )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int authorId = findFirstUserId(connection);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            statement.setInt(1, authorId);
            statement.setTimestamp(2, now);
            statement.setTimestamp(3, now);
            statement.setString(4, content);
            statement.setString(5, title);
            statement.setString(6, EMPTY_VALUE);
            statement.setString(7, "publish");
            statement.setString(8, "open");
            statement.setString(9, "open");
            statement.setString(10, EMPTY_VALUE);
            statement.setString(11, buildSlug(title));
            statement.setString(12, EMPTY_VALUE);
            statement.setString(13, EMPTY_VALUE);
            statement.setTimestamp(14, now);
            statement.setTimestamp(15, now);
            statement.setString(16, EMPTY_VALUE);
            statement.setInt(17, DEFAULT_PARENT_ID);
            statement.setString(18, EMPTY_VALUE);
            statement.setInt(19, DEFAULT_MENU_ORDER);
            statement.setString(20, "post");
            statement.setString(21, EMPTY_VALUE);
            statement.setInt(22, DEFAULT_COMMENT_COUNT);

            statement.executeUpdate();

            return getGeneratedId(statement);

        } catch (SQLException sqlException){
            throw new IllegalStateException("Не удалось создать запись в таблице wp_posts", sqlException);
        }
    }

    public void deletePostId(int postId) {
        String sql = "DELETE FROM wp_posts WHERE ID = ? AND post_type = 'post'";

        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Не удалось удалить запись из таблицы wp_posts", exception);
        }
    }
}
