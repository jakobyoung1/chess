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

}
