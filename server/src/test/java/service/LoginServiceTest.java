package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import server.Service.LoginService;
import server.requests.LoginRequest;
import server.results.LoginResult;

import java.util.HashMap;

public class LoginServiceTest {
    public static void main(String[] args) {
        LoginService service = new LoginService(new UserDAO(new HashMap<>(), new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            LoginRequest request = new LoginRequest("validUser", "validPassword");
            LoginResult result = service.login(request);

            assert result.authToken() != null : "Positive Test Failed: Expected valid authToken";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            LoginRequest request = new LoginRequest("invalidUser", "wrongPassword");
            LoginResult result = service.login(request);

            assert result == null : "Negative Test Failed: Expected null for invalid login";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
