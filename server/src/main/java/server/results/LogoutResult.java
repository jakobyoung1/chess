package server.results;

public record LogoutResult(String username, String authToken, String message) {
    public LogoutResult(String username, String authToken) {
        this(username, authToken, "Logout successful");  // Success case
    }

    public LogoutResult(String message) {
        this(null, null, message);
    }
}