package server.handlers;

import com.google.gson.Gson;
import server.service.ListGamesService;
import requests.ListGamesRequest;
import results.ListGamesResult;
import dataaccess.AuthDAO;
import model.AuthData;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final ListGamesService listGamesService;
    private final AuthDAO authDAO;

    public ListGamesHandler(ListGamesService listGamesService, AuthDAO authDAO) {
        this.listGamesService = listGamesService;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        System.out.println("Listing games: ");
        Gson gson = new Gson();

        String authToken = req.headers("Authorization");

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            res.status(401);
            return gson.toJson(new ListGamesResult("Error: Unauthorized"));
        }

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = listGamesService.listGames(request);

        res.type("application/json");
        return gson.toJson(result);
    }
}
