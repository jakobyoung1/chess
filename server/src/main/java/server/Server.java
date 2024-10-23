package server;

import server.Service.*;
import server.handlers.*;
import spark.Spark;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

import java.util.HashMap;

public class Server {

    public int run(int port) {
        Spark.staticFiles.location("/web");

        Spark.port(port);

        UserDAO userDAO = new UserDAO(new HashMap<>(), new HashMap<>());
        GameDAO gameDAO = new GameDAO(new HashMap<>());
        AuthDAO authDAO = new AuthDAO(new HashMap<>());

        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        LoginService logService = new LoginService(userDAO, authDAO);
        LogoutService outService = new LogoutService(userDAO, authDAO);
        RegistrationService regService = new RegistrationService(userDAO, authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO);
        StartGameService startGameService = new StartGameService(gameDAO);
        JoinGameService joinGameService = new JoinGameService(gameDAO);

        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegisterHandler(regService));
        Spark.post("/session", new LoginHandler(logService));
        Spark.delete("/session", new LogoutHandler(outService));
        Spark.get("/game", new ListGamesHandler(listGamesService, authDAO));
        Spark.post("/game", new StartGameHandler(startGameService, authDAO));
        Spark.put("/game", new JoinGameHandler(joinGameService, authDAO));


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}
