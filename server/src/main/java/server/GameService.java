package server;

import dataaccess.GameDAO;
import model.GameData;
import model.GameMove;
import model.AuthData;
import java.sql.Connection;

public class GameService {
    private final GameDAO gameDAO;

    // Constructor that accepts a Connection object
    public GameService(Connection connection) {
        this.gameDAO = new GameDAO(connection);
    }

    // Start a new game: Takes a request and returns a result
    public StartGameResult startGame(StartGameRequest request) throws DataAccessException {
        GameData newGame = new GameData(request.player1(), request.player2());
        gameDAO.insertGame(newGame);

        return new StartGameResult(newGame.getGameId(), newGame.getInitialBoardState(), "Game started successfully");
    }

    // Make a move in the game: Takes a request and returns a result
    public MoveResult makeMove(MoveRequest request) throws DataAccessException {
        GameData game = gameDAO.getGame(request.gameId());

        if (game == null) {
            return new MoveResult("Error: Game not found");
        }

        GameMove move = new GameMove(request.player(), request.move());
        boolean isValid = game.isValidMove(move);

        if (!isValid) {
            return new MoveResult("Error: Invalid move");
        }

        game.makeMove(move);
        gameDAO.updateGame(game);

        return new MoveResult(game.getGameId(), game.getBoardState(), "Move made successfully");
    }

    // Get the current state of the game: Takes a request and returns a result
    public GameStateResult getGameState(GameStateRequest request) throws DataAccessException {
        GameData game = gameDAO.getGame(request.gameId());

        if (game == null) {
            return new GameStateResult("Error: Game not found");
        }

        return new GameStateResult(game.getGameId(), game.getBoardState(), "Game state retrieved successfully");
    }
}
