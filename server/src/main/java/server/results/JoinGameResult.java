package server.results;

public record JoinGameResult(int gameId, String message) {
    public String getMessage() {
        return message;
    }
}
