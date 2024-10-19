package server;

import dataaccess.*;
import model.UserData;
import model.AuthData;
import java.sql.Connection;

public class UserService {
    private final UserDAO userDAO;

    // Constructor that accepts a Connection object
    public UserService(Connection connection) {
        this.userDAO = new UserDAO(connection);
    }

    // Register service method receiving a request object and returning a result object
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData user = new UserData(request.username(), request.password(), request.email());

        if (user.getUsername() == null || user.getPassword() == null) {
            return new RegisterResult("Error: Username and password must not be null");
        }

        if (userDAO.getUser(user.getUsername()) != null) {
            return new RegisterResult("Error: Username already taken");
        }

        userDAO.insertUser(user);
        AuthData authData = generateAuthData(user.getUsername());

        return new RegisterResult(authData.username(), authData.authToken());
    }

    // Login service method receiving a request object and returning a result object
    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser == null) {
            return new LoginResult("Error: User not found");
        }

        if (!request.password().equals(existingUser.getPassword())) {
            return new LoginResult("Error: Invalid credentials");
        }

        AuthData authData = generateAuthData(existingUser.getUsername());

        return new LoginResult(authData.username(), authData.authToken());
    }

    // Logout service method receiving a request object
    public void logout(AuthData auth) {
        userDAO.invalidateSession(auth.authToken());
    }

    // Helper function to generate authentication data (e.g., auth tokens)
    private AuthData generateAuthData(String username) {
        String token = java.util.UUID.randomUUID().toString();  // Generate a token
        return new AuthData(username, token);
    }
}
