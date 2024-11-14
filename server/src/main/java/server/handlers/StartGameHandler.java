package server.handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import model.AuthData;
import server.service.StartGameService;
import requests.StartGameRequest;
import results.StartGameResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class StartGameHandler implements Route {
    private final StartGameService startGameService;
    private final AuthDAO authDAO;

    public StartGameHandler(StartGameService startGameService, AuthDAO authDAO) {
        this.startGameService = startGameService;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        String authToken = req.headers("Authorization");

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            res.status(401);
            return gson.toJson(createErrorResponse(null, "Error: Unauthorized"));
        }

        StartGameRequest request = gson.fromJson(req.body(), StartGameRequest.class);

        StartGameResult result = startGameService.startGame(request);

        res.type("application/json");

        if (result.getMessage().contains("Error: Unauthorized")) {
            res.status(401);
        } else if (result.getMessage().contains("Error")) {
            res.status(400);
        } else {
            res.status(200);
        }

        String jsonResponse = gson.toJson(createCustomResponse(result));
        System.out.println("StartGameHandler response JSON: " + jsonResponse);

        return jsonResponse;
    }

    private Map<String, Object> createCustomResponse(StartGameResult result) {
        Map<String, Object> response = new HashMap<>();
        response.put("gameID", result.getGameId());  // Map 'gameId' to 'id' for the test framework
        response.put("gameData", result.getGameData());
        response.put("message", result.getMessage());
        return response;
    }

    private Map<String, Object> createErrorResponse(Integer gameId, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("gameID", gameId);
        response.put("gameData", null);
        response.put("message", message);
        return response;
    }
}
