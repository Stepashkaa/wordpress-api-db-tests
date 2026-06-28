package ru.simbirsoft.db;

import java.sql.*;
import java.time.LocalDateTime;
import static ru.simbirsoft.db.DbDataHelper.*;


public class CommentDataRepository {

    private static final String EMPTY_VALUE = "";

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
}
