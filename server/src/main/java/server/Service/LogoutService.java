package server.Service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import server.requests.LogoutRequest;
import server.results.LogoutResult;

public class LogoutService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LogoutService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        try {
            authDAO.deleteAuth(request.authToken());

            return new LogoutResult("Logout successful");
        } catch (DataAccessException e) {
            return new LogoutResult("Error: Invalid auth token");
        }
    }
}
