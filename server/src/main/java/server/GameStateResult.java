package server;

import chess.ChessGame;

public record GameStateResult(Integer gameId, ChessGame game, String message) {
    public GameStateResult(String message) {
        this(null, null, message);  // Error case
    }
}
