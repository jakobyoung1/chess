package server.service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.requests.JoinGameRequest;
import server.results.JoinGameResult;

public class JoinGameService {
    private final GameDAO gameDAO;

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
