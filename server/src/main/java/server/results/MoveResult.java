package server.results;

import chess.ChessGame;

public record MoveResult(Integer gameId, ChessGame game, String message) {
    public MoveResult(String message) {
        this(null, null, message);  // Error case
    }
}
