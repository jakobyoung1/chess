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

        String expectedMessage = "Error inserting game: Duplicate entry '1' for key";
        assertTrue(exception.getMessage().contains(expectedMessage), "Unexpected error message: " + exception.getMessage());
    }


    @Test
    public void testGetGameSuccess() {
        GameData game = new GameData(1, "whitePlayer", "blackPlayer", "Test Game");
        assertDoesNotThrow(() -> gameDAO.createGame(game));

        GameData retrievedGame = assertDoesNotThrow(() -> gameDAO.getGame(1));
        assertNotNull(retrievedGame, "Expected to retrieve a game, but got null");

        assertEquals("whitePlayer", retrievedGame.getWhiteUsername(), "White username does not match");
        assertEquals("blackPlayer", retrievedGame.getBlackUsername(), "Black username does not match");
        assertEquals("Test Game", retrievedGame.getGameName(), "Game name does not match");
        assertNotNull(retrievedGame.getGame(), "Expected a ChessGame object, but got null");
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
    public void testUpdateGameFailure() {
        GameData nonExistentGame = new GameData(99, "noWhite", "noBlack", "No Game");
        ChessGame updatedChessGame = new ChessGame();
        nonExistentGame.setGame(updatedChessGame);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameDAO.updateGame(99, nonExistentGame));

        assertEquals("Game not found", exception.getMessage());
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
    public void testListGamesEmpty() {
        assertDoesNotThrow(() -> gameDAO.clear());

        List<GameData> games = assertDoesNotThrow(() -> gameDAO.listGames());
        assertNotNull(games, "Expected an empty list, but got null");
        assertTrue(games.isEmpty(), "Expected an empty list of games, but found some games");
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
