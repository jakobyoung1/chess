package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.JoinGameService;
import server.requests.JoinGameRequest;
import server.results.JoinGameResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {

    private GameDAO gameDAO;
    private JoinGameService service;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new GameDAO();
        service = new JoinGameService(gameDAO);
        GameData game1 = new GameData(1, null, null, "Test Game 1");
        gameDAO.createGame(game1);
    }

    @Test
    public void testJoinGamePositive() {
        JoinGameRequest req = new JoinGameRequest("WHITE", "player1", 1);
        JoinGameResult res = null;
        try {
            res = service.joinGame(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertEquals("Joined game successfully", res.getMessage(), "Positive Test Failed");
    }

    @Test
    public void testJoinGameInvalidGameId() {
        JoinGameRequest req = new JoinGameRequest("BLACK", "player2", -1);

        JoinGameResult res = null;
        try {
            res = service.joinGame(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }
        assertEquals("Error: Invalid game ID"  , res.getMessage(), "Positive Test Failed");


    }

    @Test
    public void testJoinGamePlayerColorTaken() {
        // First join game as WHITE
        JoinGameRequest req1 = new JoinGameRequest("WHITE", "player1", 1);
        try {
            service.joinGame(req1);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        // Attempt to join game as WHITE again
        JoinGameRequest req2 = new JoinGameRequest("WHITE", "player3", 1);
        JoinGameResult res = null;
        try {
            res = service.joinGame(req2);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }
        assertEquals("Error: Color taken", res.getMessage(), "Positive Test Failed");
    }
}
