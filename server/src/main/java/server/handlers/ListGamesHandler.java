package server.handlers;

import com.google.gson.Gson;
import server.GameService;
import server.requests.ListGamesRequest;
import server.results.ListGamesResult;
import dataaccess.AuthDAO;
import model.AuthData;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final GameService gameService;
    private final AuthDAO authDAO;

    public ListGamesHandler(GameService gameService, AuthDAO authDAO) {
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
            return gson.toJson(new ListGamesResult("Error: Unauthorized"));
        }

        ListGamesRequest request = new ListGamesRequest();
        ListGamesResult result = gameService.listGames(request);

        res.type("application/json");
        return gson.toJson(result);
    }
}
