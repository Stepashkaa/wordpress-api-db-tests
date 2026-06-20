package ru.simbirsoft.db;

import io.qameta.allure.Step;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CommentRepository {

    @Step("Получить комментарий из таблицы wp_comments по ID: {id}")
    public Optional<CommentDbRecord> finalById(int id){
        String sql = """
                SELECT comment_ID, comment_post_ID, comment_author, comment_author_email, comment_content, comment_approved
                FROM wp_comments
                WHERE comment_ID = ?
                """;
        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(new CommentDbRecord(
                            resultSet.getInt("comment_ID"),
                            resultSet.getInt("comment_post_ID"),
                            resultSet.getString("comment_author"),
                            resultSet.getString("comment_author_email"),
                            resultSet.getString("comment_content"),
                            resultSet.getString("comment_approved")
                    ));
                }
            }

            return Optional.empty();
        } catch (SQLException sqlException){
            throw new IllegalStateException("Ошибка при получении комментария из wp_comments", sqlException);
        }
    }

    @Step("Проверить существование комментария в wp_comments по ID: {id}")
    public boolean existsById(int id){
        String sql = """
                    SELECT COUNT(*)
                    FROM wp_comments
                    WHERE comment_ID = ?
                """;
        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()){
                resultSet.next();

                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException sqlException){
            throw new IllegalStateException("Ошибка при проверке существования комментария", sqlException);
        }
    }

    @Step("Посчитать количество комментариев в wp_comments")
    public long countAll(){
        String sql = """
                SELECT COUNT(*)
                FROM wp_comments
                """;

        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException exception) {
            throw new IllegalStateException("Ошибка при подсчёте комментариев", exception);
        }
    }
}
