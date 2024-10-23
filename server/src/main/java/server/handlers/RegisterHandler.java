package server.handlers;

import com.google.gson.Gson;
import server.RegistrationService;
import server.UserService;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final RegistrationService regService;

    public RegisterHandler(RegistrationService regService) {
        this.regService = regService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

        RegisterResult result = regService.register(request);

        res.type("application/json");
        if (result.message().contains("Error: Username and password must not be null")) {
            res.status(400);
        } else if (result.message().contains("Error: Username already taken")) {
            res.status(403);
        } else {
            res.status(200);
        }
        return gson.toJson(result);
    }
}
