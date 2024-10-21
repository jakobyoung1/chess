package server.handlers;

import com.google.gson.Gson;
import server.GameService;
import server.requests.StartGameRequest;
import server.results.StartGameResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class StartGameHandler implements Route {
    private final GameService gameService;

    public StartGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        // Deserialize the start game request
        StartGameRequest request = gson.fromJson(req.body(), StartGameRequest.class);

        // Call the GameService to start the game
        StartGameResult result = gameService.startGame(request);

        // Set response content type to JSON and return the result
        res.type("application/json");
        return gson.toJson(result);
    }
}
