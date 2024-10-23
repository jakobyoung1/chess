package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.Service.ClearService;
import server.results.ClearResult;

import java.util.HashMap;

public class ClearServiceTest {
    public static void main(String[] args) {

        ClearService service = new ClearService(new UserDAO(new HashMap<>(), new HashMap<>()), new GameDAO(new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            ClearResult res = service.clear();
            assert res.message().equals("Clear successful") : "Failed Positive Test";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("PT e: " + e.getMessage());
        }

        try {
            ClearResult res = service.clear();
            assert res.message().contains("Error") : "Failed Negative Test";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("NT e: " + e.getMessage());
        }
    }
}
