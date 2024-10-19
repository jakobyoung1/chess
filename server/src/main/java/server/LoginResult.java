package server;

public record LoginResult(String username, String authToken, String message) {
    public LoginResult(String username, String authToken) {
        this(username, authToken, null);  // Success case
    }

    public LoginResult(String message) {
        this(null, null, message);  // Error case
    }
}