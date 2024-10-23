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

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            return new RegisterResult("Error: Username and password must not be null");
        }

        if (userDAO.getUser(request.username()) != null) {
            return new RegisterResult("Error: Username already taken");
        }
        System.out.println("inserting username: " + request.username());

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.insertUser(newUser);

        AuthData authData = generateAuthToken(newUser.getUsername());
        authDAO.createAuth(authData);

        return new RegisterResult(authData.username(), authData.authToken(), "User registered successfully");
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser == null) {
            return new LoginResult("Error: User not found");
        }

        if (!existingUser.getPassword().equals(request.password())) {
            return new LoginResult("Error: Invalid password");
        }

        AuthData authData = generateAuthToken(existingUser.getUsername());
        authDAO.createAuth(authData);

        return new LoginResult(authData.username(), authData.authToken(), "Login successful");
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        try {
            authDAO.deleteAuth(request.authToken());

            return new LogoutResult("Logout successful");
        } catch (DataAccessException e) {
            return new LogoutResult("Error: Invalid auth token");
        }
    }


    private AuthData generateAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
