package server.handlers;

import com.google.gson.Gson;
import server.service.ClearService;
import results.ClearResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        ClearResult result = clearService.clear();

        res.type("application/json");
        return gson.toJson(result);
    }
}
