package server;

public record GameStateResult(String gameId, String boardState, String message) {
    public GameStateResult(String message) {
        this(null, null, message);  // Error case
    }
}
