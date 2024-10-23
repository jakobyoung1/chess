package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.service.JoinGameService;
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

            // trying a positive test
            try {
                JoinGameRequest req = new JoinGameRequest("WHITE", "player1", 1);
                JoinGameResult res = service.joinGame(req);
                assert res.getMessage().equals("Joined game") : "Positive Test Failed";
                System.out.println("Positive Test Passed");
            } catch (DataAccessException e) {
                System.out.println("Positive Test Exception: " + e.getMessage());
            }

            // trying a negative test
            try {
                JoinGameRequest req = new JoinGameRequest("BLACK", "player2", -1);
                JoinGameResult res = service.joinGame(req);
                assert res.getMessage().contains("Error: Invalid game ID") : "Negative Test Failed";
                System.out.println("Negative Test Passed");
            } catch (DataAccessException e) {
                System.out.println("Negative Test Exception: " + e.getMessage());
            }

            // trying a negative test
            try {
                JoinGameRequest req = new JoinGameRequest("WHITE", "player3", 1);  // White already taken
                JoinGameResult res = service.joinGame(req);
                assert res.getMessage().contains("Error: Player color taken") : "Negative Test Failed";
                System.out.println("Negative Test Passed");
            } catch (DataAccessException e) {
                System.out.println("Negative Test Exception: " + e.getMessage());
            }

        } catch (DataAccessException e) {
            System.out.println("Create game failed: " + e.getMessage());
        }
    }
}
