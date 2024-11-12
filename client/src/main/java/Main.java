import client.ServerFacade;
import server.Server;
import ui.PostLoginUI;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
        System.out.println("Server started on port " + port);

        String serverURL = "http://localhost:" + port;
        ServerFacade serverFacade = new ServerFacade(serverURL);

        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

        String authToken = preLoginUI.getAuthToken();
        if (authToken != null) {
            PostLoginUI postLoginUI = new PostLoginUI(serverFacade, authToken);
            postLoginUI.display();
        }

        server.stop();
    }
}
