package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.service.LoginService;
import requests.LoginRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private static LoginService service;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        service = new LoginService(userDAO, authDAO);

        // Add a valid user for testing
        userDAO.insertUser(new UserData("validUser", "validPassword", "validUser@example.com"));
    }

    @Test
    public void testLoginPositive() {
        LoginRequest req = new LoginRequest("validUser", "validPassword");
        LoginResult res = null;
        try {
            res = service.login(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "LoginResult should not be null");
        assertNotNull(res.authToken(), "AuthToken should not be null");
        assertEquals("validUser", res.username(), "Username should match the logged in user");
    }

    @Test
    public void testLoginNegative() {
        LoginRequest req = new LoginRequest("invalidUser", "wrongPassword");

        LoginResult res = null;
        try {
            res = service.login(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }
        assertEquals("Error: User not found", res.message(), "Positive Test Failed");

    }
}
