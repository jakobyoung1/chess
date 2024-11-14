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
    private static final AtomicInteger NEXTGAMEID = new AtomicInteger(1);

    public StartGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {

        int gameId = NEXTGAMEID.getAndIncrement();
        System.out.println("starting game: " + gameId);

        if (request.gameName() == "") {
            return new StartGameResult(null, null, "Error: no game name provided");
        }

        GameData newGame = new GameData(gameId, null, null, request.gameName());
        ChessGame game = new ChessGame();
        gameDAO.createGame(newGame);

        System.out.println("just made this game: " + newGame.getGameId());

        return new StartGameResult(newGame.getGameId(), newGame, "Game started successfully");
    }
}
