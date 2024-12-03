package server;

import dataaccess.DataAccessException;
import server.service.*;
import server.handlers.*;
import spark.Spark;
import dataaccess.*;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class Server {

    private static boolean initialized = false;

    public int run(int port) throws DataAccessException {
        if (!initialized) {
            Spark.port(port);

            UserDAO userDAO = new UserDAO();
            GameDAO gameDAO = new GameDAO();
            AuthDAO authDAO = new AuthDAO();

            ChessWebSocketHandler webSocketHandler = new ChessWebSocketHandler(gameDAO, authDAO);

            Spark.webSocket("/ws", webSocketHandler);

            Spark.staticFiles.location("/web");

            try {
                DatabaseManager.createDatabase();
                System.out.println("Database initialized successfully.");
            } catch (DataAccessException e) {
                System.out.println("Database not initialized. " + e.getMessage());
                return -1;
            }

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

            Spark.awaitInitialization();
            initialized = true;
        }
        return Spark.port();
    }

    public void stop() {
        if (initialized) {
            System.out.println("Stopping the server...");
            Spark.stop();
            Spark.awaitStop();
            initialized = false;
            System.out.println("Server stopped successfully.");
        }
    }
}
