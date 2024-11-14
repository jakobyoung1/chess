package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clearDB();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        var result = facade.register("testUser", "testPassword", "test@example.com");
        assertNotNull(result);
        assertNotNull(result.authToken());
    }

    @Test
    void testRegisterFailureDuplicateUser() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("testUser", "testPassword", "test@example.com");
        });
        assertEquals("Error: Not successful", exception.getMessage());
    }

    @Test
    void testLoginSuccess() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        var result = facade.logIn("testUser", "testPassword");
        assertNotNull(result);
        assertNotNull(result.authToken());
    }

    @Test
    void testLoginFailureWrongPassword() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.logIn("testUser", "wrongPassword");
        });
        assertEquals("Error: Not successful", exception.getMessage());
    }

    @Test
    void testLogoutSuccess() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        var result = facade.logout();
        assertNotNull(result);
    }

    @Test
    void testLogoutFailureNoAuthToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.logout();
        });
        assertEquals("Error: Not successful", exception.getMessage());
    }

    @Test
    void testCreateGameSuccess() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        var result = facade.createGame("Test Game");
        assertNotNull(result);
    }

    @Test
    void testCreateGameFailureNoAuthToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame("Test Game");
        });
        assertEquals("Error: Not successful", exception.getMessage());
    }


    @Test
    void testListGamesSuccess() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        facade.createGame("Test Game 1");
        facade.createGame("Test Game 2");
        var result = facade.listGames();
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    void testListGamesFailureNoAuthToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.listGames();
        });
        assertEquals("Error: Not successful", exception.getMessage());
    }

    @Test
    void testJoinGameSuccess() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        var game = facade.createGame("Test Game");
        System.out.println(game.getGameId());
        assertNotNull(game);

        var result = facade.joinGame(game.getGameId(), "testUser", "WHITE");
        assertNotNull(result);
        assertNotNull(result.message());
    }

    @Test
    void testJoinGameFailureInvalidGameID() throws Exception {
        facade.register("testUser", "testPassword", "test@example.com");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(9999, "testUser", "white");
        });
        assertEquals("Error: Not successful", exception.getMessage());
    }

}
