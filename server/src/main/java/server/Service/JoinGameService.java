package server.Service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.requests.JoinGameRequest;
import server.results.JoinGameResult;

import java.util.concurrent.atomic.AtomicInteger;

public class JoinGameService {
    private final GameDAO gameDAO;
    private static final AtomicInteger nextGameId = new AtomicInteger(1);

    public JoinGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        System.out.println("joinGameResult ID: " + request.gameID());

        if (request.gameID() <= 0) {
            return new JoinGameResult(request.gameID(), "Error: Invalid game ID");
        }

        if (request.playerColor() == null ||
                (!"WHITE".equals(request.playerColor()) && !"BLACK".equals(request.playerColor()))) {
            return new JoinGameResult(request.gameID(), "Error: Invalid color");
        }

        GameData game = gameDAO.getGame(request.gameID());

        if (game == null) {
            return new JoinGameResult(request.gameID(), "Error: Game not found");
        }

        if ("WHITE".equals(request.playerColor()) && game.getWhiteUsername() == null) {
            game.setWhiteUsername(request.username());
        } else if ("BLACK".equals(request.playerColor()) && game.getBlackUsername() == null) {
            game.setBlackUsername(request.username());
        } else {
            return new JoinGameResult(request.gameID(), "Error: Color taken");
        }

        gameDAO.updateGame(game.getGameId(), game.getGame());

        return new JoinGameResult(request.gameID(), "Joined game successfully");
    }

}
