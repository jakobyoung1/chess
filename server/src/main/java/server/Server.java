package server;

import dataaccess.DataAccessException;
import server.service.*;
import server.handlers.*;
import spark.Spark;
import dataaccess.*;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

import java.util.HashMap;

public class Server {

    public int run(int port) {
        Spark.staticFiles.location("/web");

        Spark.port(port);

        try {
            DatabaseManager.createDatabase();
            System.out.println("Database initialized successfully.");
        } catch (DataAccessException e) {
            System.out.println("Database not initialized. " + e.getMessage());
            return -1;
        }

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

        Spark.delete("/db", new ClearHandler(clearService));              // Clear the database
        Spark.post("/user", new RegisterHandler(regService));             // Register a user
        Spark.post("/session", new LoginHandler(logService));             // Login (create session)
        Spark.delete("/session", new LogoutHandler(outService));          // Logout (remove session)
        Spark.get("/game", new ListGamesHandler(listGamesService, authDAO)); // List games
        Spark.post("/game", new StartGameHandler(startGameService, authDAO)); // Start a game
        Spark.put("/game", new JoinGameHandler(joinGameService, authDAO)); // Join a game

        Spark.get("/", (req, res) -> {
            res.status(404);
            return "Not Found";
        });

        Spark.awaitInitialization();

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}
