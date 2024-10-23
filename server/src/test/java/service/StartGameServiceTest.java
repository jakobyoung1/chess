package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.service.StartGameService;
import server.requests.StartGameRequest;
import server.results.StartGameResult;

import java.util.HashMap;

public class StartGameServiceTest {
    public static void main(String[] args) {
        StartGameService service = new StartGameService(new GameDAO(new HashMap<>()));

        //pos test
        try {
            StartGameRequest req = new StartGameRequest("ChessMasterGame", "player1", "player2");
            StartGameResult res = service.startGame(req);

            assert res.getGameId() > 0 : "Positive Test Failed";

            GameData gameData = res.getGameData();
            assert "ChessMasterGame".equals(gameData.getGameName()) : "Positive Test Failed";

            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        //neg test
        try {
            StartGameRequest req = new StartGameRequest("", "player1", "player2");
            StartGameResult res = service.startGame(req);

            assert res == null : "Negative Test Failed";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
