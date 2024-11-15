import client.ServerFacade;
import ui.PostLoginUI;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade(8080);
        System.out.println("Welcome to Chess! Please select one of the following commands:");
        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

        String authToken = preLoginUI.getAuthToken();
        String username = preLoginUI.getUsername();
        if (authToken != null && !authToken.isEmpty()) {
            while(true) {
                authToken = preLoginUI.getAuthToken();
                PostLoginUI postLoginUI = new PostLoginUI(serverFacade, authToken, username);
                postLoginUI.display();
                preLoginUI.setAuthToken();
                preLoginUI.display();
            }
        } else {
            System.out.println("Login failed or canceled. Returning to PreLoginUI.");
        }
    }
}
