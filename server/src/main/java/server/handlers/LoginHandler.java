package server.handlers;

import com.google.gson.Gson;
import server.service.LoginService;
import requests.LoginRequest;
import results.LoginResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final LoginService logService;

    public LoginHandler(LoginService logService) {
        this.logService = logService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        System.out.println("Logging in...");
        Gson gson = new Gson();

        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

        LoginResult result = logService.login(request);

        res.type("application/json");
        if (result.message().contains("Error")) {
            res.status(401);
        } else {
            res.status(200);
        }
        return gson.toJson(result);
    }
}
