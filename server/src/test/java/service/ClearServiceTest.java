package service;

import dataaccess.*;
import model.*;
import server.ClearService;
import server.results.ClearResult;

import java.util.HashMap;

public class ClearServiceTest {

    private static HashMap<String, UserData> users = new HashMap<>();
    private static HashMap<String, AuthData> auths = new HashMap<>();
    private static HashMap<Integer, GameData> games = new HashMap<>();
    private static UserDAO userDAO = new UserDAO(users, auths);
    private static GameDAO gameDAO = new GameDAO(games);
    private static AuthDAO authDAO = new AuthDAO(auths);
    private static ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

    public static void main(String[] args) {
        try {
            testClearSuccess();
            System.out.println("All tests passed!");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }

    public static void testClearSuccess() throws DataAccessException {
        userDAO.insertUser(new UserData("user1", "password", "user1@example.com"));
        gameDAO.createGame(new GameData(1, "player1", "player2", "Test Game"));
        authDAO.createAuth(new AuthData("user1", "token123"));

        System.out.println("Before clear - Users: " + users + ", Games: " + games + ", Auths: " + auths);

        ClearResult result = clearService.clear();

        if (result == null) {
            throw new RuntimeException("ClearResult is null");
        }
        if (!"Successfully cleared all data".equals(result.message())) {
            throw new RuntimeException("Clear success message is incorrect");
        }

        if (!users.isEmpty() || !games.isEmpty() || !auths.isEmpty()) {
            throw new RuntimeException("Clear operation failed: Data still exists.");
        }

        System.out.println("After clear - Users: " + users + ", Games: " + games + ", Auths: " + auths);
        System.out.println("testClearSuccess passed!");
    }
}
