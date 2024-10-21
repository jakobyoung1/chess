package server.handlers;

import com.google.gson.Gson;
import server.GameService;
import server.requests.ListGamesRequest;
import server.results.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        ListGamesRequest request = new ListGamesRequest();

        ListGamesResult result = gameService.listGames(request);

        res.type("application/json");
        return gson.toJson(result);
    }
}
