package ru.simbirsoft.db;

import io.qameta.allure.Step;
import ru.simbirsoft.model.PostModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PostRepository {

    @Step("Получить запись из таблицы wp_posts по ID: {id}")
    public Optional<PostModel> findById(int id){
        String sql = """
                SELECT ID, post_title, post_content, post_status, post_type
                FROM wp_posts
                WHERE ID = ?
                """;
        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(new PostModel(
                            resultSet.getInt("ID"),
                            resultSet.getString("post_title"),
                            resultSet.getString("post_content"),
                            resultSet.getString("post_status"),
                            resultSet.getString("post_type")
                    ));
                }
            }

            return Optional.empty();
        } catch (SQLException sqlException){
            throw new IllegalStateException("Ошибка при получении записии wp_posts", sqlException);
        }
    }

    @Step("Проверить существование записи в wp_posts по ID: {id}")
    public boolean existsById(int id){
        String sql = """
                    SELECT COUNT(*)
                    FROM wp_posts
                    WHERE ID = ?
                """;
        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()){
                resultSet.next();

                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException sqlException){
            throw new IllegalStateException("Ошибка при проверке существования записи в wp_posts", sqlException);
        }
    }

    @Step("Посчитать объекты в wp_posts с типом {postType} и статусом {postStatus}")
    public long countByTypeAndStatus(String postType, String postStatus){
        String sql = """
                SELECT COUNT(*)
                FROM wp_posts
                WHERE post_type = ? AND post_status = ?
                """;

        try (Connection connection = DatabaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, postType);
            statement.setString(2, postStatus);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Ошибка при подсчёте записей в wp_posts", exception);
        }
    }
}
