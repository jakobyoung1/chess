package server.service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import requests.JoinGameRequest;
import results.JoinGameResult;

public class JoinGameService {
    private final GameDAO gameDAO;

    public JoinGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        System.out.println("joinGameResult ID: " + request.gameID());

        // Validate the game ID
        if (request.gameID() <= 0) {
            return new JoinGameResult(request.gameID(), "Error: Invalid game ID");
        }

        // Validate the player's chosen color
        if (request.playerColor() == null ||
                (!"WHITE".equals(request.playerColor()) && !"BLACK".equals(request.playerColor()))) {
            return new JoinGameResult(request.gameID(), "Error: Invalid color");
        }

        // Fetch the game data
        GameData game = gameDAO.getGame(request.gameID());

        if (game == null) {
            return new JoinGameResult(request.gameID(), "Error: Game not found");
        }

        // Allow the same player (same username) to rejoin the game as the same color
        if ("WHITE".equals(request.playerColor())) {
            if (game.getWhiteUsername() == null || game.getWhiteUsername().equals(request.playerColor())) {
                // Assign the player to White if unoccupied
                game.setWhiteUsername(request.username());
            } else if (!game.getWhiteUsername().equals(request.username())) {
                // If the spot is taken by another player, return an error
                return new JoinGameResult(request.gameID(), "Error: Color taken");
            }
        } else if ("BLACK".equals(request.playerColor())) {
            if (game.getBlackUsername() == null || game.getWhiteUsername().equals(request.playerColor())) {
                // Assign the player to Black if unoccupied
                game.setBlackUsername(request.username());
            } else if (!game.getBlackUsername().equals(request.username())) {
                // If the spot is taken by another player, return an error
                return new JoinGameResult(request.gameID(), "Error: Color taken");
            }
        }

        // Update the game in the database
        gameDAO.updateGame(game.getGameId(), game);

        return new JoinGameResult(request.gameID(), "Joined game successfully");
    }
}