package server.service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.requests.*;
import server.results.*;

import java.util.UUID;

public class RegistrationService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegistrationService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            return new RegisterResult("Error: null username or password");
        }

        if (userDAO.getUser(request.username()) != null) {
            return new RegisterResult("Error: Username taken");
        }
        System.out.println("inserting username: " + request.username());

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.insertUser(newUser);

        AuthData authData = generateAuthToken(newUser.getUsername());
        authDAO.createAuth(authData);

        return new RegisterResult(authData.username(), authData.authToken(), "User registered successfully");
    }

    private AuthData generateAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
