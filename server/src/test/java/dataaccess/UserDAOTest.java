package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        userDAO.clear();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    public void testInsertUserSuccess() throws DataAccessException {
        UserData user = new UserData("testUser", "password","email");
        userDAO.insertUser(user);
        UserData retrievedUser = userDAO.getUser("testUser");

        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.getUsername());
        assertEquals("password", retrievedUser.getPassword());
    }

    @Test
    public void testInsertUserDuplicate() {
        UserData user1 = new UserData("testUser", "password1", "email1");
        UserData user2 = new UserData("testUser", "password2", "email2");

        assertDoesNotThrow(() -> userDAO.insertUser(user1));
        assertThrows(DataAccessException.class, () -> userDAO.insertUser(user2));  // Expect failure on duplicate
    }

    @Test
    public void testGetUserNotFound() throws DataAccessException {
        UserData retrievedUser = userDAO.getUser("nonExistentUser");
        assertNull(retrievedUser);
    }
}
