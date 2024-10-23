package model;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;

public class GameData {
    private final int gameId;
    private String whiteUsername;
    private String blackUsername;
    private ChessGame game;
    private final String gameName;

    public GameData(int gameId, String whiteUsername, String blackUsername, String gameName) {
        this.gameId = gameId;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = new ChessGame();
        this.gameName = gameName;
        System.out.println("created new GameData with id: " + gameId);
    }

    public int getGameId() {
        return gameId;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        game.makeMove(move);
    }

    public ChessGame getGame() {
        return game;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public void setWhiteUsername(String user) {
        whiteUsername = user;
    }

    public void setBlackUsername(String user) {
        blackUsername = user;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }
}
