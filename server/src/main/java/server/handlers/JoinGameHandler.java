package server.handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import model.AuthData;
import server.GameService;
import server.JoinGameService;
import server.results.JoinGameResult;
import server.requests.JoinGameRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final JoinGameService joinGameService;
    private final AuthDAO authDAO;

    public JoinGameHandler(JoinGameService joinGameService, AuthDAO authDAO) {
        this.joinGameService = joinGameService;
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
        System.out.println("Joining as: " + username);

        JoinGameRequest updatedRequest = new JoinGameRequest(request.playerColor(), username, request.gameID());

        JoinGameResult result = joinGameService.joinGame(updatedRequest);

        res.type("application/json");

        if (result.message().contains("Error: Invalid player color")) {
            res.status(400);
        } else if (result.message().contains("Error: Player color already taken")) {
            res.status(403);
        } else if (result.message().contains("Error")) {
            res.status(400);
        } else {
            res.status(200);
        }

        return gson.toJson(result);
    }
}
