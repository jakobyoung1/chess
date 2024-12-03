import chess.*;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import server.Server;

public class Main {

    public static void main(String[] args) throws DataAccessException {
        Server s = new Server();
        int port = s.run(8080);
    }
}