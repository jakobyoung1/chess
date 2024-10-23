package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.requests.StartGameRequest;
import server.results.StartGameResult;

import java.util.concurrent.atomic.AtomicInteger;

public class StartGameService {
    private final GameDAO gameDAO;
    private static final AtomicInteger nextGameId = new AtomicInteger(1);

    public StartGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {

        int gameId = nextGameId.getAndIncrement();
        System.out.println("starting game: " + gameId);

        GameData newGame = new GameData(gameId, null, null, request.gameName());
        ChessGame game = new ChessGame();
        gameDAO.createGame(newGame);

        System.out.println("just made this game: " + newGame.getGameId());

        return new StartGameResult(newGame.getGameId(), game, "Game started successfully");
    }
}
