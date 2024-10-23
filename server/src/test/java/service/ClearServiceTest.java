package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.ClearService;
import server.results.ClearResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService service;

    @Test
    public void testClearServicePositive() throws DataAccessException {
        UserDAO userDAO = new UserDAO(new HashMap<>(), new HashMap<>());
        userDAO.insertUser(new UserData("user","pass","email"));
        service = new ClearService(
                userDAO,
                new GameDAO(new HashMap<>()),
                new AuthDAO(new HashMap<>())
        );
        ClearResult res = null;
        try {
            res = service.clear();
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }
        assertEquals("Cleared all data", res.message(), "Positive Test Failed");
    }

    @Test
    public void testClearServiceNegative() {
        service = new ClearService(
                new UserDAO(new HashMap<>(), new HashMap<>()),
                new GameDAO(new HashMap<>()),
                new AuthDAO(new HashMap<>())
        );
        ClearResult res = null;
        try {
            res = service.clear();
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }
        assertTrue(res.message().contains("Error"), "Negative Test Failed: Expected an error message");
    }
}
