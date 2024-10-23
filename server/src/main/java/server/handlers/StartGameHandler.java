package server.handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import model.AuthData;
import server.GameService;
import server.requests.StartGameRequest;
import server.results.StartGameResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class StartGameHandler implements Route {
    private final GameService gameService;
    private final AuthDAO authDAO;

    public StartGameHandler(GameService gameService, AuthDAO authDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        String authToken = req.headers("Authorization");

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            res.status(401);
            return gson.toJson(new StartGameResult(0, null, "Error: Unauthorized"));
        }
        StartGameRequest request = gson.fromJson(req.body(), StartGameRequest.class);

        StartGameResult result = gameService.startGame(request);

        res.type("application/json");

        if (result.getMessage().contains("Error: Unauthorized")) {
            res.status(401);
        } else if (result.getMessage().contains("Error")) {
            res.status(400);
        } else {
            res.status(200);
        }

        return gson.toJson(result);
    }
}
