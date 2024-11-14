package server.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser == null) {
            return new LoginResult("Error: User not found");
        }

        if (!BCrypt.checkpw(request.password(), existingUser.getPassword())) {
            return new LoginResult("Error: Invalid password");
        }

        AuthData authData = generateAuthToken(existingUser.getUsername());
        authDAO.createAuth(authData);

        return new LoginResult(authData.username(), authData.authToken(), "Login successful");
    }

    private AuthData generateAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
