package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import requests.StartGameRequest;
import results.StartGameResult;

import java.util.concurrent.atomic.AtomicInteger;

public class StartGameService {
    private final GameDAO gameDAO;
    private static AtomicInteger nextgameid;

    public StartGameService(GameDAO gameDAO) throws DataAccessException {
        this.gameDAO = gameDAO;
        int maxGameId = gameDAO.getMaxGameId();
        nextgameid = new AtomicInteger(maxGameId + 1);
        System.out.println("Initialized nextgameid to " + nextgameid.get());
    }

    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {

        int gameId = nextgameid.getAndIncrement();
        System.out.println("starting game: " + gameId);

        if (request.gameName() == null || request.gameName().isEmpty()) {
            return new StartGameResult(null, null, "Error: no game name provided");
        }
        GameData newGame = new GameData(gameId, null, null, request.gameName());

        gameDAO.createGame(newGame);

        System.out.println("just made this game: " + newGame.getGameId());

        return new StartGameResult(newGame.getGameId(), newGame, "Game started successfully");
    }
}
