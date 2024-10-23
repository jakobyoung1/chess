package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import server.service.ListGamesService;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;

import java.util.HashMap;

public class ListGamesServiceTest {
    public static void main(String[] args) throws DataAccessException {
        GameDAO gameDAO = new GameDAO(new HashMap<>());
        gameDAO.createGame(new GameData(1, "player1", "player2", "Game 1"));
        gameDAO.createGame(new GameData(2, "player3", "player4", "Game 2"));

        ListGamesService service = new ListGamesService(gameDAO);

        // trying a positive test
        try {
            ListGamesRequest req = new ListGamesRequest();
            ListGamesResult res = service.listGames(req);

            assert res.getGames().size() == 2 : "Positive Test Failed";
            System.out.println("Positive Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Positive Test Exception: " + e.getMessage());
        }

        //negative test
        try {
            gameDAO.clear();

            ListGamesRequest req = new ListGamesRequest();
            ListGamesResult res = service.listGames(req);

            assert res.getGames().size() == 0 : "Negative Test Failed";
            System.out.println("Negative Test Passed");
        } catch (DataAccessException e) {
            System.out.println("Negative Test Exception: " + e.getMessage());
        }
    }
}
