package dataaccess;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        try {
            authDAO.clear();
            userDAO.clear();

            UserData user = new UserData("testUser", "hashedPassword", "test@example.com");
            userDAO.insertUser(user);

        } catch (DataAccessException e) {
            fail("Failed to set up database state: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            authDAO.clear();
            userDAO.clear();
        } catch (DataAccessException e) {
            fail("Failed to clear tables in teardown: " + e.getMessage());
        }
    }

    @Test
    public void testCreateAuthSuccess() {
        AuthData auth = new AuthData("testToken", "testUser");
        assertDoesNotThrow(() -> authDAO.createAuth(auth));

        AuthData retrievedAuth = assertDoesNotThrow(() -> authDAO.getAuth("testToken"));
        assertNotNull(retrievedAuth);
        assertEquals("testToken", retrievedAuth.authToken());
        assertEquals("testUser", retrievedAuth.username());
    }

    @Test
    public void testCreateAuthDuplicate() {
        AuthData auth1 = new AuthData("duplicateToken", "testUser");
        AuthData auth2 = new AuthData("duplicateToken", "testUser");

        assertDoesNotThrow(() -> authDAO.createAuth(auth1));
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth2));
    }

    @Test
    public void testGetAuthSuccess() {
        AuthData authData = new AuthData("validAuthToken", "testUser");
        assertDoesNotThrow(() -> authDAO.createAuth(authData));

        AuthData retrievedAuth = assertDoesNotThrow(() -> authDAO.getAuth("validAuthToken"));

        assertNotNull(retrievedAuth, "Expected to retrieve auth data, but got null");
        assertEquals("validAuthToken", retrievedAuth.authToken(), "Auth token mismatch");
        assertEquals("testUser", retrievedAuth.username(), "Username mismatch");
    }

    @Test
    public void testGetAuthNotFound() {
        AuthData retrievedAuth = assertDoesNotThrow(() -> authDAO.getAuth("nonExistentToken"));
        assertNull(retrievedAuth);
    }

    @Test
    public void testDeleteAuthSuccess() {
        AuthData auth = new AuthData("deletableToken", "testUser");
        assertDoesNotThrow(() -> authDAO.createAuth(auth));

        assertDoesNotThrow(() -> authDAO.deleteAuth("deletableToken"));
        AuthData retrievedAuth = assertDoesNotThrow(() -> authDAO.getAuth("deletableToken"));
        assertNull(retrievedAuth); // Should be null after deletion
    }

    @Test
    public void testDeleteAuthNotFound() {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("nonExistentToken"));
    }

    @Test
    public void testClear() {
        AuthData auth1 = new AuthData("token1", "testUser");
        AuthData auth2 = new AuthData("token2", "testUser");

        assertDoesNotThrow(() -> authDAO.createAuth(auth1));
        assertDoesNotThrow(() -> authDAO.createAuth(auth2));

        assertDoesNotThrow(() -> authDAO.clear());

        AuthData retrievedAuth1 = assertDoesNotThrow(() -> authDAO.getAuth("token1"));
        AuthData retrievedAuth2 = assertDoesNotThrow(() -> authDAO.getAuth("token2"));
        assertNull(retrievedAuth1);
        assertNull(retrievedAuth2);
    }
}
