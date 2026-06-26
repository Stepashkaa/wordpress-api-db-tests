package ru.simbirsoft.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public class DbDataRepository {
    
    private static final int DEFAULT_PARENT_ID = 0;
    private static final int DEFAULT_MENU_ORDER = 0;
    private static final int DEFAULT_COMMENT_COUNT = 0;
    private static final String EMPTY_VALUE = "";

    public int createPublishedPost(String title, String content) {
        return createPublishedPostEntity(title, content, "post");
    }

    public int createPublishedPage(String title, String content) {
        return createPublishedPostEntity(title, content, "page");
    }

    public int createApprovedCommentEntity(int postId, String author, String email, String content) {
        String sql = """
                INSERT INTO wp_comments(comment_post_ID,
                                        comment_author,
                                        comment_author_email,
                                        comment_author_url,
                                        comment_author_IP,
                                        comment_date,
                                        comment_date_gmt,
                                        comment_content,
                                        comment_karma,
                                        comment_approved,
                                        comment_agent,
                                        comment_type,
                                        comment_parent,
                                        user_id
                                    )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseClient.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            statement.setInt(1, postId);
            statement.setString(2, author);
            statement.setString(3, email);
            statement.setString(4, EMPTY_VALUE);
            statement.setString(5, "127.0.0.1");
            statement.setTimestamp(6, now);
            statement.setTimestamp(7, now);
            statement.setString(8, content);
            statement.setInt(9, 0);
            statement.setString(10, "1");
            statement.setString(11, "D2 autotest");
            statement.setString(12, "comment");
            statement.setInt(13, 0);
            statement.setInt(14, 0);

            statement.executeUpdate();

            return getGeneratedId(statement);
        } catch (SQLException sqlException){
            throw new IllegalStateException("Не удалось создать комментарий в таблице wp_comments", sqlException);
        }
    }

    public void deleteCommentId(int commentId){
        String sql = "DELETE FROM wp_comments WHERE comment_ID = ?";

        try (Connection connection = DatabaseClient.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, commentId);
            statement.executeUpdate();
        } catch(SQLException sqlException){
            throw new IllegalStateException("Не удалось удалить комментарий из таблицы wp_comments", sqlException);
        }
    }

    public void deletePostId(int postId) {
        String sql = "DELETE FROM wp_posts WHERE ID = ?";

        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Не удалось удалить запись из таблицы wp_posts", exception);
        }
    }

    private int createPublishedPostEntity(String title, String content, String type) {
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
            statement.setString(20, type);
            statement.setString(21, EMPTY_VALUE);
            statement.setInt(22, DEFAULT_COMMENT_COUNT);

            statement.executeUpdate();

            return getGeneratedId(statement);

        } catch (SQLException sqlException){
            throw new IllegalStateException("Не удалось создать объект в таблице wp_posts", sqlException);
        }
    }

    public int findFirstUserId(Connection connection) throws SQLException{
        String sql = """
                SELECT ID FROM wp_users ORDER BY ID LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if(resultSet.next()){
                return resultSet.getInt("ID");
            }

            throw new IllegalStateException("В таблице wp_users не найден пользователь для поля post_author");
        }
    }

    private int getGeneratedId(PreparedStatement statement) throws SQLException{
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            if(resultSet.next()){
                return resultSet.getInt(1);
            }

            throw new IllegalStateException("База данных не вернула ID созданной тестовой записи");
        }
    }

    private String buildSlug(String title){
        return title.toLowerCase()
                .replace("_", "-")
                .replaceAll("[^a-z0-9а-яё-]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "")
                + "-" + System.currentTimeMillis();
    }
}
