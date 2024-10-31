package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            String createDatabaseStatement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
                try (var pStatement = conn.prepareStatement(createDatabaseStatement)) {
                    pStatement.executeUpdate();
                }

                conn.setCatalog(DATABASE_NAME);

                String createUserTable = """
                    CREATE TABLE IF NOT EXISTS User (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password_hash VARCHAR(60) NOT NULL,
                        email VARCHAR(100) NOT NULL
                    );
                """;
                try (var pStatement = conn.prepareStatement(createUserTable)) {
                    pStatement.executeUpdate();
                }

                String createGameTable = """
                    CREATE TABLE IF NOT EXISTS Game (
                        game_id INT PRIMARY KEY,
                        white_username VARCHAR(50) NULL,
                        black_username VARCHAR(50) NULL,
                        game_name VARCHAR(100) NOT NULL,
                        game_state JSON NOT NULL
                    );
                """;
                try (var p = conn.prepareStatement(createGameTable)) {
                    p.executeUpdate();
                }

                String createAuthTable = """
                    CREATE TABLE IF NOT EXISTS Auth (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        auth_token VARCHAR(255) UNIQUE NOT NULL,
                        username VARCHAR(50) NOT NULL
                    );
                """;

                try (var p = conn.prepareStatement(createAuthTable)) {
                    p.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
