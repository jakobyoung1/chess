package server.handlers;

import com.google.gson.Gson;
import server.UserService;
import server.requests.LogoutRequest;
import server.results.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();

        // Deserialize the logout request (containing the authToken)
        LogoutRequest request = gson.fromJson(req.body(), LogoutRequest.class);

        // Call the UserService to log out the user
        LogoutResult result = userService.logout(request);

        // Set response content type to JSON and return the result
        res.type("application/json");
        return gson.toJson(result);
    }
}
