package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import server.Service.RegistrationService;
import server.requests.RegisterRequest;
import server.results.RegisterResult;

import java.util.HashMap;

public class RegistrationServiceTest {
    public static void main(String[] args) {
        RegistrationService service = new RegistrationService(new UserDAO(new HashMap<>(), new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            RegisterRequest request = new RegisterRequest("newUser", "newPassword", "newUser@mail.com");
            RegisterResult result = service.register(request);

            assert result.authToken() != null : "Positive Test Failed: Expected valid authToken";
            assert "newUser".equals(result.username()) : "Positive Test Failed: Expected newUser as username";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            RegisterRequest request = new RegisterRequest("existingUser", "existingPassword", "existingUser@mail.com");
            RegisterResult result = service.register(request);

            assert result == null || result.message().contains("Error: already taken") : "Negative Test Failed: Expected error for existing username";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
