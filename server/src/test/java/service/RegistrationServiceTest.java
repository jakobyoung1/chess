package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.RegistrationService;
import server.requests.RegisterRequest;
import server.results.RegisterResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTest {

    private RegistrationService service;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        userDAO.clear();
        service = new RegistrationService(userDAO, new AuthDAO());
        // Insert an existing user for the negative test case
        userDAO.insertUser(new UserData("existingUser", "existingPassword", "existingUser@mail.com"));
    }

    @Test
    public void testRegisterNewUserPositive() {
        RegisterRequest req = new RegisterRequest("newUser", "newPassword", "newUser@mail.com");
        RegisterResult res = null;

        try {
            res = service.register(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "RegisterResult should not be null");
        assertNotNull(res.authToken(), "AuthToken should not be null for new user");
        assertEquals("newUser", res.username(), "Username should match the new user's name");
    }

    @Test
    public void testRegisterExistingUserNegative() {
        RegisterRequest req = new RegisterRequest("existingUser", "existingPassword", "existingUser@mail.com");
        RegisterResult res = null;

        try {
            res = service.register(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "RegisterResult should not be null for existing user");
        assertTrue(res.message().contains("Error: Username taken"), "Should return an error message for an existing user");
    }
}
