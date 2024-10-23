package server;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ListGamesService {
    private final GameDAO gameDAO;
    private static final AtomicInteger nextGameId = new AtomicInteger(1);

    public ListGamesService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        try {
            List<GameData> games = gameDAO.listGames();

            if (games == null || games.isEmpty()) {
                return new ListGamesResult(new ArrayList<>());
            }

            System.out.println(games.get(0).getGameId());
            return new ListGamesResult(games);
        } catch (DataAccessException e) {
            return new ListGamesResult("Error: Unable to retrieve game list - " + e.getMessage());
        }
    }

}
