package server.handlers;

import com.google.gson.Gson;
import server.GameService;
import server.results.*;
import server.requests.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

        JoinGameResult result = gameService.joinGame(request);

        res.type("application/json");
        return gson.toJson(result);
    }
}
