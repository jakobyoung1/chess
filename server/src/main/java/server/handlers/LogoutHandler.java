package server.handlers;

import com.google.gson.Gson;
import server.Service.LogoutService;
import server.requests.LogoutRequest;
import server.results.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final LogoutService outService;

    public LogoutHandler(LogoutService outService) {
        this.outService = outService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        String authToken = req.headers("Authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(400);
            return gson.toJson(new LogoutResult("Error: Missing auth token"));
        }

        LogoutRequest request = new LogoutRequest(authToken);

        LogoutResult result = outService.logout(request);

        res.type("application/json");

        if (result.message().contains("Error")) {
            res.status(401);
        } else {
            res.status(200);
        }

        return gson.toJson(result);
    }
}
