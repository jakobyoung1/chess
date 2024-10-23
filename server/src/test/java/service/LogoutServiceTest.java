package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import server.Service.LogoutService;
import server.requests.LogoutRequest;
import server.results.LogoutResult;

import java.util.HashMap;

public class LogoutServiceTest {
    public static void main(String[] args) {
        LogoutService service = new LogoutService(new UserDAO(new HashMap<>(), new HashMap<>()), new AuthDAO(new HashMap<>()));
        try {
            LogoutRequest request = new LogoutRequest("validAuthToken");
            LogoutResult result = service.logout(request);

            assert result != null : "Positive Test Failed: Expected successful logout";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            LogoutRequest request = new LogoutRequest("invalidAuthToken");
            LogoutResult result = service.logout(request);

            assert result == null : "Negative Test Failed: Expected null result for invalid authToken";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
