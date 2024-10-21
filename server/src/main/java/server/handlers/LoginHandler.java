package server.handlers;

import com.google.gson.Gson;
import server.UserService;
import server.requests.LoginRequest;
import server.results.LoginResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        // Deserialize the login request
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

        // Call the UserService to log in the user
        LoginResult result = userService.login(request);

        // Set response content type to JSON and return the result
        res.type("application/json");
        return gson.toJson(result);
    }
}
