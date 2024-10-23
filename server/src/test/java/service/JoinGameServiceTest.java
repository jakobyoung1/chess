package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.Service.JoinGameService;
import server.requests.JoinGameRequest;
import server.results.JoinGameResult;

import java.util.HashMap;

public class JoinGameServiceTest {
    public static void main(String[] args) {

        GameDAO gameDAO = new GameDAO(new HashMap<>());
        JoinGameService service = new JoinGameService(gameDAO);

        GameData game1 = new GameData(1, null, null, "Test Game 1");
        try {
            gameDAO.createGame(game1);

            try {
                JoinGameRequest request = new JoinGameRequest("WHITE", "player1", 1);
                JoinGameResult result = service.joinGame(request);
                assert result.getMessage().equals("Joined game successfully") : "Positive Test Failed: Expected success message";
                System.out.println("Positive Test Passed");
            } catch (DataAccessException e) {
                System.out.println("Positive Test Exception: " + e.getMessage());
            }

            try {
                JoinGameRequest request = new JoinGameRequest("BLACK", "player2", -1);  // Invalid game ID
                JoinGameResult result = service.joinGame(request);
                assert result.getMessage().contains("Error: Invalid game ID") : "Negative Test Failed: Expected invalid game ID error";
                System.out.println("Negative Test Passed");
            } catch (DataAccessException e) {
                System.out.println("Negative Test Exception: " + e.getMessage());
            }

            try {
                JoinGameRequest request = new JoinGameRequest("WHITE", "player3", 1);  // White already taken
                JoinGameResult result = service.joinGame(request);
                assert result.getMessage().contains("Error: Player color already taken") : "Negative Test Failed: Expected player color taken error";
                System.out.println("Negative Test Passed");
            } catch (DataAccessException e) {
                System.out.println("Negative Test Exception: " + e.getMessage());
            }

        } catch (DataAccessException e) {
            System.out.println("Game creation failed: " + e.getMessage());
        }
    }
}
