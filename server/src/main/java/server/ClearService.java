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
        this.authDAO = new AuthDAO(connection);
    }

    public ClearResult clear() throws DataAccessException {
        try {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();

            return new ClearResult("Successfully cleared all data");

        } catch (DataAccessException e) {
            return new ClearResult("Error: Failed to clear data - " + e.getMessage());
        }
    }
}
