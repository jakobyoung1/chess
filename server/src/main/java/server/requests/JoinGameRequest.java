package server.requests;

public record JoinGameRequest(String playerColor, String username, int gameID) {
}
