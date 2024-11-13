import client.ServerFacade;
import server.Server;
import ui.PostLoginUI;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);

        ServerFacade serverFacade = new ServerFacade(port);

        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

        String authToken = preLoginUI.getAuthToken();
        String username = preLoginUI.getUsername();
        if (authToken != null) {
            PostLoginUI postLoginUI = new PostLoginUI(serverFacade, authToken, username);
            postLoginUI.display();
        }

        server.stop();
    }
}
