package server;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;
import java.sql.Connection;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;

    public UserService(Connection connection) {
        this.userDAO = new UserDAO(connection);
    }

    // Method for registering a new user
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        // Ensure username and password are provided
        if (request.username() == null || request.password() == null) {
            return new RegisterResult("Error: Username and password must not be null");
        }

        // Check if the username is already taken
        if (userDAO.getUser(request.username()) != null) {
            return new RegisterResult("Error: Username already taken");
        }

        // Create a new user and store it in the database
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.insertUser(newUser);

        // Generate an authentication token for the new user
        AuthData authData = generateAuthToken(request.username());

        return new RegisterResult(authData.username(), authData.authToken(), "User registered successfully");
    }

    // Method for logging in an existing user
    public LoginResult login(LoginRequest request) throws DataAccessException {
        // Retrieve the user by username
        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser == null) {
            return new LoginResult("Error: User not found");
        }

        // Validate the provided password
        if (!existingUser.getPassword().equals(request.password())) {
            return new LoginResult("Error: Invalid password");
        }

        // Generate a new authentication token
        AuthData authData = generateAuthToken(existingUser.getUsername());

        return new LoginResult(authData.username(), authData.authToken(), "Login successful");
    }

    // Method for logging out a user
    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        // Invalidate the session by removing the auth token
        userDAO.invalidateSession(request.authToken());

        return new LogoutResult("Logout successful");
    }

    // Helper method to generate an authentication token
    private AuthData generateAuthToken(String username) {
        String authToken = UUID.randomUUID().toString();  // Generate a unique token
        return new AuthData(username, authToken);
    }
}
