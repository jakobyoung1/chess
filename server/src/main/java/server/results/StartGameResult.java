package server.results;

import chess.ChessGame;

public class StartGameResult {
    private final int gameID;
    private final ChessGame game;
    private final String message;

    public StartGameResult(int gameID, ChessGame game, String message) {
        this.gameID = gameID;
        this.game = game;
        this.message = message;
    }

    public int getGameId() {
        return gameID;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }
}
