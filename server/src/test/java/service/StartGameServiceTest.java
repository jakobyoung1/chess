package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.Service.StartGameService;
import server.requests.StartGameRequest;
import server.results.StartGameResult;

import java.util.HashMap;

public class StartGameServiceTest {
    public static void main(String[] args) {
        StartGameService service = new StartGameService(new GameDAO(new HashMap<>()));

        try {
            StartGameRequest request = new StartGameRequest("ChessMasterGame", "player1", "player2");
            StartGameResult result = service.startGame(request);

            assert result.getGameId() > 0 : "Positive Test Failed: Expected valid game ID";

            GameData gameData = result.getGameData();
            assert "ChessMasterGame".equals(gameData.getGameName()) : "Positive Test Failed: Expected game name to match";

            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            StartGameRequest request = new StartGameRequest("", "player1", "player2");
            StartGameResult result = service.startGame(request);

            assert result == null : "Negative Test Failed: Expected null result for empty game name";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
