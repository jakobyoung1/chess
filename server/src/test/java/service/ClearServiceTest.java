package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.Test;
import server.Service.ClearService;
import server.results.ClearResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    public void testClearServicePositive() {
        ClearService service = new ClearService(new UserDAO(new HashMap<>(), new HashMap<>()), new GameDAO(new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            ClearResult res = service.clear();
            assertEquals("Clear successful", res.message(), "Failed Positive Test");
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            fail("PT e: " + e.getMessage());
        }
    }

    @Test
    public void testClearServiceNegative() {
        ClearService service = new ClearService(new UserDAO(new HashMap<>(), new HashMap<>()), new GameDAO(new HashMap<>()), new AuthDAO(new HashMap<>()));

        try {
            ClearResult res = service.clear();
            assertTrue(res.message().contains("Error"), "Failed Negative Test");
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            fail("NT e: " + e.getMessage());
        }
    }
}
