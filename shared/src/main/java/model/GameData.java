package model;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;

public class GameData {
    private final int gameId;
    private final String whiteUsername;
    private final String blackUsername;
    private final ChessGame game;
    private final String gameName;

    public GameData(int gameId, String whiteUsername, String blackUsername, String gameName) {
        this.gameId = gameId;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = new ChessGame();
        this.gameName = gameName;
    }

    public int getGameId() {
        return gameId;
    }

    // The new makeMove method
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Call the makeMove method from the ChessGame class to update the board
        game.makeMove(move);
    }

    public ChessGame getGame() {
        return game;
    }

    public String getGameName() {
        return gameName;
    }

}
