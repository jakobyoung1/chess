package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.ClearService;
import server.results.ClearResult;

import java.util.HashMap;

public class ClearServiceTest {
    public static void main(String[] args) {

        ClearService service = new ClearService(new UserDAO(new HashMap<>(), new HashMap<>()), new GameDAO(new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            ClearResult result = service.clear();
            assert result.message().equals("Clear successful") : "Positive Test Failed: Expected success message";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            ClearResult result = service.clear();
            assert result.message().contains("Error") : "Negative Test Failed: Expected error message";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
