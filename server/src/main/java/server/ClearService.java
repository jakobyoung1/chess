package server;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import java.sql.Connection;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(Connection connection) {
        this.userDAO = new UserDAO(connection);
        this.gameDAO = new GameDAO(connection);
        this.authDAO = new AuthDAO(connection);  // Assuming you have an AuthDAO for managing authentication tokens
    }

    // Method to clear all data (users, games, and auth tokens)
    public ClearResult clear() throws DataAccessException {
        try {
            // Clear users
            userDAO.clear();
            // Clear games
            gameDAO.clear();
            // Clear auth tokens
            authDAO.clear();

            return new ClearResult("Successfully cleared all data");

        } catch (DataAccessException e) {
            return new ClearResult("Error: Failed to clear data - " + e.getMessage());
        }
    }
}
