package server;

public record RegisterResult(String username, String authToken, String message) {
    public RegisterResult(String username, String authToken) {
        this(username, authToken, null);  // Success case
    }

    public RegisterResult(String message) {
        this(null, null, message);  // Error case
    }
}
