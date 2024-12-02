package model;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;

public class GameData {
    private final int gameID;
    private String whiteUsername;
    private String blackUsername;
    private ChessGame game;
    private final String gameName;
    private boolean gameOver; // Added game over status

    public GameData(int gameId, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameId;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = new ChessGame();
        this.gameName = gameName;
        this.gameOver = false; // Default to false
    }

    // Getters
    public int getGameId() {
        return gameID;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Setters
    public void setGame(ChessGame game) {
        this.game = game;
    }

    public void setWhiteUsername(String user) {
        whiteUsername = user;
    }

    public void setBlackUsername(String user) {
        blackUsername = user;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    // Methods
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (gameOver) {
            throw new InvalidMoveException("Cannot make a move. The game is already over.");
        }
        game.makeMove(move);
    }
}
