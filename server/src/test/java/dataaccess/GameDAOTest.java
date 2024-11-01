package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAO();
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            fail("Failed to clear Game table in setup: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            fail("Failed to clear Game table in teardown: " + e.getMessage());
        }
    }

    @Test
    public void testCreateGameSuccess() {
        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Test Game");
        assertDoesNotThrow(() -> gameDAO.createGame(game));

        GameData retrievedGame = assertDoesNotThrow(() -> gameDAO.getGame(1));
        assertNotNull(retrievedGame);
        assertEquals("whitePlayer", retrievedGame.getWhiteUsername());
        assertEquals("blackPlayer", retrievedGame.getBlackUsername());
        assertEquals("Test Game", retrievedGame.getGameName());
        assertNotNull(retrievedGame.getGame()); // Ensure ChessGame object is deserialized
    }

    @Test
    public void testCreateGameFailure() {
        GameData game1 = new GameData(1, "whitePlayer", "blackPlayer", "Test Game");
        assertDoesNotThrow(() -> gameDAO.createGame(game1));

        GameData game2 = new GameData(1, "anotherWhitePlayer", "anotherBlackPlayer", "Another Test Game");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameDAO.createGame(game2));

        assertEquals("Error inserting game: Duplicate entry '1' for key 'game.PRIMARY'", exception.getMessage());
    }


    @Test
    public void testGetGameNotFound() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameDAO.getGame(99));
        assertEquals("Game not found", exception.getMessage());
    }


    @Test
    public void testUpdateGameSuccess() {
        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Test Game");
        assertDoesNotThrow(() -> gameDAO.createGame(game));

        ChessGame updatedChessGame = new ChessGame();
        game.setGame(updatedChessGame);

        assertDoesNotThrow(() -> gameDAO.updateGame(1, game));

        GameData retrievedGame = assertDoesNotThrow(() -> gameDAO.getGame(1));
        assertNotNull(retrievedGame);
        assertEquals("whitePlayer", retrievedGame.getWhiteUsername());
        assertEquals("blackPlayer", retrievedGame.getBlackUsername());
        assertEquals("Test Game", retrievedGame.getGameName());
        assertNotNull(retrievedGame.getGame());
    }

    @Test
    public void testListGames() {
        GameData game1 = new GameData(1, "whitePlayer1", "blackPlayer1", "Game 1");
        GameData game2 = new GameData(2, "whitePlayer2", "blackPlayer2", "Game 2");

        assertDoesNotThrow(() -> gameDAO.createGame(game1));
        assertDoesNotThrow(() -> gameDAO.createGame(game2));

        List<GameData> games = assertDoesNotThrow(() -> gameDAO.listGames());
        assertEquals(2, games.size());

        GameData retrievedGame1 = games.get(0);
        GameData retrievedGame2 = games.get(1);

        assertEquals("whitePlayer1", retrievedGame1.getWhiteUsername());
        assertEquals("blackPlayer2", retrievedGame2.getBlackUsername());
    }

    @Test
    public void testClear() {
        GameData game1 = new GameData(1, "whitePlayer1", "blackPlayer1", "Game 1");
        GameData game2 = new GameData(2, "whitePlayer2", "blackPlayer2", "Game 2");

        assertDoesNotThrow(() -> gameDAO.createGame(game1));
        assertDoesNotThrow(() -> gameDAO.createGame(game2));

        assertDoesNotThrow(() -> gameDAO.clear());

        List<GameData> games = assertDoesNotThrow(() -> gameDAO.listGames());
        assertEquals(0, games.size());
    }
}
