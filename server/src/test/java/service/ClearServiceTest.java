package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.service.ClearService;
import results.ClearResult;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService service;

    @Test
    public void testClearServicePositive() throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        userDAO.insertUser(new UserData("user","pass","email"));
        service = new ClearService(
                userDAO,
                new GameDAO(),
                new AuthDAO()
        );
        ClearResult res = null;
        try {
            res = service.clear();
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }
        assertEquals("Cleared all data", res.message(), "Positive Test Failed");
    }
}
