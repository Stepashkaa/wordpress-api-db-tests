package ru.simbirsoft.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbDataHelper {
    private DbDataHelper() {
    }

    static int findFirstUserId(Connection connection) throws SQLException {
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

    static int getGeneratedId(PreparedStatement statement) throws SQLException{
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            if(resultSet.next()){
                return resultSet.getInt(1);
            }

            throw new IllegalStateException("База данных не вернула ID созданной тестовой записи");
        }
    }

    static String buildSlug(String title){
        return title.toLowerCase()
                .replace("_", "-")
                .replaceAll("[^a-z0-9а-яё-]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "")
                + "-" + System.currentTimeMillis();
    }
}
