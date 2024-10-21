package server;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import server.results.ClearResult;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
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
