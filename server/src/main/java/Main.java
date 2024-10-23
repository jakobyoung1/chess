import chess.*;
import server.Server;

public class Main {

    public static void main(String[] args) {
        Server s = new Server();
        int port = s.run(8080);
    }
}