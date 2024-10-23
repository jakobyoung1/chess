package server.handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;  // Make sure to import AuthDAO
import model.AuthData;
import server.GameService;
import server.results.JoinGameResult;
import server.requests.JoinGameRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final GameService gameService;
    private final AuthDAO authDAO;

    public JoinGameHandler(GameService gameService, AuthDAO authDAO) {
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
            return gson.toJson(new JoinGameResult(0, "Error: Unauthorized"));
        }

        String username = authData.username();

        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

        System.out.println("handler received gameID: " + request.gameID());
        System.out.println("Joining as WHITE: " + username);

        JoinGameRequest updatedRequest = new JoinGameRequest(request.playerColor(), username, request.gameID());

        JoinGameResult result = gameService.joinGame(updatedRequest);

        res.type("application/json");
        return gson.toJson(result);
    }
}
