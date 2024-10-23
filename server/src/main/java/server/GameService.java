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

    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {

        int gameId = nextGameId.getAndIncrement();
        System.out.println("starting game: " + gameId);

        GameData newGame = new GameData(gameId, null, null, request.gameName());
        ChessGame game = new ChessGame();
        gameDAO.createGame(newGame);

        System.out.println("just made this game: " + newGame.getGameId());

        return new StartGameResult(newGame.getGameId(), game, "Game started successfully");
    }


    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        System.out.println("joinGameResult ID: " + request.gameID());

        if (request.gameID() <= 0) {
            return new JoinGameResult(request.gameID(), "Error: Invalid game ID");
        }

        if (request.playerColor() == null ||
                (!"WHITE".equals(request.playerColor()) && !"BLACK".equals(request.playerColor()))) {
            return new JoinGameResult(request.gameID(), "Error: Invalid player color");
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
            return new JoinGameResult(request.gameID(), "Error: Player color already taken");
        }

        gameDAO.updateGame(game.getGameId(), game.getGame());

        return new JoinGameResult(request.gameID(), "Joined game successfully");
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

            if (games == null || games.isEmpty()) {
                return new ListGamesResult(new ArrayList<>());
            }

            System.out.println(games.get(0).getGameId());
            return new ListGamesResult(games);
        } catch (DataAccessException e) {
            return new ListGamesResult("Error: Unable to retrieve game list - " + e.getMessage());
        }
    }



}
