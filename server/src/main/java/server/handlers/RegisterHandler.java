package server.handlers;

import com.google.gson.Gson;
import server.UserService;
import server.requests.RegisterRequest;
import server.results.RegisterResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

        RegisterResult result = userService.register(request);

        res.type("application/json");
        return gson.toJson(result);
    }
}
