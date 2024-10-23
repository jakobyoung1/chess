package dataaccess;

import java.sql.SQLException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {

    // Constructor that takes both a message and an SQL exception (existing one)
    public DataAccessException(String message, SQLException e) {
        super(message, e);
    }

    // New constructor that takes only a message (for non-SQL related errors)
    public DataAccessException(String message) {
        super(message);
    }
}

