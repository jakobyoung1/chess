package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import server.service.LoginService;
import server.requests.LoginRequest;
import server.results.LoginResult;

import java.util.HashMap;

public class LoginServiceTest {
    public static void main(String[] args) {
        LoginService service = new LoginService(new UserDAO(new HashMap<>(), new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            LoginRequest req = new LoginRequest("validUser", "validPassword");
            LoginResult res = service.login(req);

            assert res.authToken() != null : "Positive Test Failed";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            LoginRequest req = new LoginRequest("invalidUser", "wrongPassword");
            LoginResult res = service.login(req);

            assert res == null : "Negative Test Failed";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
