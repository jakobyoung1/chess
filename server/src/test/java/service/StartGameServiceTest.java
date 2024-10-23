package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.StartGameService;
import server.requests.StartGameRequest;
import server.results.StartGameResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class StartGameServiceTest {

    private StartGameService service;
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAO(new HashMap<>());
        service = new StartGameService(gameDAO);
    }

    @Test
    public void testStartGamePositive() {
        StartGameRequest req = new StartGameRequest("player1", "player2", "ChessMasterGame");
        StartGameResult res = null;

        try {
            res = service.startGame(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertNotNull(res, "StartGameResult should not be null for a valid game");
        assertTrue(res.getGameId() > 0, "Game ID should be greater than 0 for a valid game");

        GameData gameData = res.getGameData();
        assertEquals("ChessMasterGame", gameData.getGameName(), "Game name should match the provided game name");
    }

    @Test
    public void testStartGameNegative() {
        StartGameRequest req = new StartGameRequest("player1", "player2", "");
        StartGameResult res = null;

        try {
            res = service.startGame(req);
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException: " + e.getMessage());
        }

        assertEquals("Error: no game name provided", res.getMessage(), "No game should have been created");
    }
}
