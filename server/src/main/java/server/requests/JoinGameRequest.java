package server.requests;

public record JoinGameRequest(String playerColor, String playerName, int gameId) {
}
