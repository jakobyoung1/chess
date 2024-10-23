package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import server.service.LogoutService;
import server.requests.LogoutRequest;
import server.results.LogoutResult;

import java.util.HashMap;

public class LogoutServiceTest {
    public static void main(String[] args) {
        LogoutService service = new LogoutService(new UserDAO(new HashMap<>(), new HashMap<>()), new AuthDAO(new HashMap<>()));

        // positive test
        try {
            LogoutRequest req = new LogoutRequest("validAuthToken");
            LogoutResult res = service.logout(req);

            assert res != null : "Positive Test Failed";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        // negative test
        try {
            LogoutRequest req = new LogoutRequest("invalidAuthToken");
            LogoutResult res = service.logout(req);

            assert res == null : "Negative Test Failed";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
