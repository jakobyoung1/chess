import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.Server;
import server.service.*;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        Server server = new Server();
        int port = server.run(8080);

        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        LoginService loginService = new LoginService(userDAO, authDAO);
        RegistrationService registrationService = new RegistrationService(userDAO, authDAO);
//        LogoutService logoutService = new LogoutService(userDAO, authDAO);
//        ListGamesService listGamesService = new ListGamesService(gameDAO);
//        StartGameService startGameService = new StartGameService(gameDAO);
//        JoinGameService joinGameService = new JoinGameService(gameDAO);

        PreLoginUI preLoginUI = new PreLoginUI(loginService, registrationService);
        preLoginUI.display();

        server.stop();
    }
}
