package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import server.ListGamesService;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;

import java.util.HashMap;

public class ListGamesServiceTest {
    public static void main(String[] args) throws DataAccessException {
        GameDAO gameDAO = new GameDAO(new HashMap<>());
        gameDAO.createGame(new GameData(1, "player1", "player2", "Game 1"));
        gameDAO.createGame(new GameData(2, "player3", "player4", "Game 2"));

        ListGamesService service = new ListGamesService(gameDAO);

        try {
            ListGamesRequest request = new ListGamesRequest(); // Create a ListGamesRequest object
            ListGamesResult result = service.listGames(request);

            assert result.getGames().size() == 2 : "Positive Test Failed: Expected 2 games";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        try {
            gameDAO.clear();

            ListGamesRequest request = new ListGamesRequest(); // Create a ListGamesRequest object
            ListGamesResult result = service.listGames(request);

            assert result.getGames().size() == 0 : "Negative Test Failed: Expected no games";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
