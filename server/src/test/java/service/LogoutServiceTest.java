package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.LogoutService;
import server.requests.LogoutRequest;
import server.results.LogoutResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private LogoutService service;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new AuthDAO(new HashMap<>());
        service = new LogoutService(new UserDAO(), authDAO);

        authDAO.createAuth(new AuthData("validAuthToken", "validUser"));
    }

    @Test
    public void testLogoutPositive() {
        LogoutRequest req = new LogoutRequest("validAuthToken");
        LogoutResult res = null;
        try {
            res = service.logout(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "LogoutResult should not be null");
        assertEquals(res.message(), "Logout successful", "Positive Test Passed");
    }

    @Test
    public void testLogoutNegative() {
        LogoutRequest req = new LogoutRequest("invalidAuthToken");

        LogoutResult res = null;
        try {
            res = service.logout(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "LogoutResult should not be null");
        assertNotEquals(res.message(), "Logout successful", "Negative Test Passed");
    }
}
