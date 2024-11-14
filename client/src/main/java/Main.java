import client.ServerFacade;
import ui.PostLoginUI;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade(8080);

        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

        String authToken = preLoginUI.getAuthToken();
        String username = preLoginUI.getUsername();
        if (authToken != null) {
            PostLoginUI postLoginUI = new PostLoginUI(serverFacade, authToken, username);
            postLoginUI.display();
        }
        serverFacade.stop();
    }
}
