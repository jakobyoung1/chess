package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.ListGamesService;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private GameDAO gameDAO;
    private ListGamesService service;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new GameDAO();
        gameDAO.createGame(new GameData(1, "player1", "player2", "Game 1"));
        gameDAO.createGame(new GameData(2, "player3", "player4", "Game 2"));
        service = new ListGamesService(gameDAO);
    }

    @Test
    public void testListGamesPositive() {
        ListGamesRequest req = new ListGamesRequest();
        ListGamesResult res = null;
        try {
            res = service.listGames(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "Result should not be null");
        assertEquals(2, res.getGames().size(), "Positive Test Failed: Expected 2 games");
    }

    @Test
    public void testListGamesNegative() {
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            fail("Failed to clear GameDAO: " + e.getMessage());
        }

        ListGamesRequest req = new ListGamesRequest();
        ListGamesResult res = null;
        try {
            res = service.listGames(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "Result should not be null");
        assertEquals(0, res.getGames().size(), "Negative Test Failed: Expected 0 games");
    }
}
