package server.requests;

public class LogoutRequest {
    private final String authToken;

    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }

    public String authToken() {
        return authToken;
    }
}
