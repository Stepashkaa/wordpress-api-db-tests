package ru.simbirsoft.db;

import ru.simbirsoft.config.TestConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseClient {
    private DatabaseClient() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                TestConfig.dbUrl(),
                TestConfig.dbUsername(),
                TestConfig.dbPassword()
        );
    }
}
