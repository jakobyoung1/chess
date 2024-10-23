package server.results;

import chess.ChessGame;

public class StartGameResult {
    private final int gameId;
    private final ChessGame game;
    private final String message;

    public StartGameResult(int gameId, ChessGame game, String message) {
        this.gameId = gameId;
        this.game = game;
        this.message = message;
    }

    public int getGameId() {
        return gameId;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }
}
