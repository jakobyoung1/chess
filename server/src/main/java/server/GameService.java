package server;

import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.AuthData;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final GameDAO gameDAO;
    private static final AtomicInteger nextGameId = new AtomicInteger(1);

    public GameService(Connection connection) {
        this.gameDAO = new GameDAO(connection);
    }

    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {
        int gameId = nextGameId.getAndIncrement();
        GameData newGame = new GameData(gameId, request.player1(), request.player2(), request.gameName());

        gameDAO.createGame(newGame);

        return new StartGameResult(newGame.getGameId(), newGame.getGame(), "Game started successfully");
    }

    public MoveResult makeMove(MoveRequest request) throws DataAccessException, InvalidMoveException {
        GameData game = gameDAO.getGame(request.gameId());

        if (game == null) {
            return new MoveResult("Error: Game not found");
        }

        ChessMove move = new ChessMove(request.move().getStartPosition(), request.move().getEndPosition(), request.move().getPromotionPiece());

        game.makeMove(move);
        gameDAO.updateGame(game.getGameId(), game.getGame());

        return new MoveResult(game.getGameId(), game.getGame(), "Move made successfully");
    }

    public GameStateResult getGameState(GameStateRequest request) throws DataAccessException {
        GameData game = gameDAO.getGame(request.gameId());

        if (game == null) {
            return new GameStateResult("Error: Game not found");
        }

        return new GameStateResult(game.getGameId(), game.getGame(), "Game state retrieved successfully");
    }
}
