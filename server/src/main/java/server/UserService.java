package server;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.UserData;
import model.AuthData;
import dataaccess.DataAccessException;
import server.requests.*;
import server.requests.RegisterRequest;
import server.results.*;
import server.results.LogoutResult;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
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
