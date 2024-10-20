package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.requests.GameStateRequest;
import server.requests.MoveRequest;
import server.requests.*;
import server.results.GameStateResult;
import server.results.MoveResult;
import server.results.*;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final GameDAO gameDAO;
    private static final AtomicInteger nextGameId = new AtomicInteger(1);

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {
        int gameId = nextGameId.getAndIncrement();
        GameData newGame = new GameData(gameId, null, null, request.gameName());
        ChessGame game = new ChessGame();  // Initialize the chess board or game logic
        gameDAO.createGame(newGame);

        return new StartGameResult(newGame.getGameId(), game, "Game started successfully");
    }


    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        GameData game = gameDAO.getGame(request.gameId());

        if (game == null) {
            return new JoinGameResult(request.gameId(), "Error: Game not found");
        }

        if ("WHITE".equals(request.playerColor()) && game.getWhiteUsername() == null) {
            game.setWhiteUsername(request.playerName());
        }
        else if ("BLACK".equals(request.playerColor()) && game.getBlackUsername() == null) {
            game.setBlackUsername(request.playerName());
        }
        else {
            return new JoinGameResult(request.gameId(), "Error: Player color already taken");
        }

        gameDAO.updateGame(game.getGameId(), game.getGame());

        return new JoinGameResult(request.gameId(), "Joined game successfully");
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

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        try {
            List<GameData> games = gameDAO.listGames();
            return new ListGamesResult(games);
        } catch (DataAccessException e) {
            return new ListGamesResult("Error: Unable to retrieve game list - " + e.getMessage());
        }
    }

}
