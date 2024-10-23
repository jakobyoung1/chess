package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.requests.MoveRequest;
import server.requests.*;
import server.results.MoveResult;
import server.results.*;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final GameDAO gameDAO;
    private static final AtomicInteger nextGameId = new AtomicInteger(1);

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
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





}
