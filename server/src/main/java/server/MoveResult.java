package server;

public record MoveResult(String gameId, String boardState, String message) {
    public MoveResult(String message) {
        this(null, null, message);  // Error case
    }
}
