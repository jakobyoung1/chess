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
            RegisterRequest req = new RegisterRequest("newUser", "newPassword", "newUser@mail.com");
            RegisterResult res = service.register(req);

            assert res.authToken() != null : "Positive Test Failed";
            assert "newUser".equals(res.username()) : "Positive Test Failed";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            RegisterRequest req = new RegisterRequest("existingUser", "existingPassword", "existingUser@mail.com");
            RegisterResult res = service.register(req);

            assert res == null || res.message().contains("Error: already taken") : "Negative Test Failed";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
