package client;

import com.google.gson.Gson;
import server.requests.*;
import server.results.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private final String baseUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String sendPostRequest(String endpoint, String jsonInput) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } else {
            throw new Exception("HTTP error code: " + responseCode);
        }
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String responseJson = sendPostRequest("/user", gson.toJson(request));
        return gson.fromJson(responseJson, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        String responseJson = sendPostRequest("/session", gson.toJson(request));
        return gson.fromJson(responseJson, LoginResult.class);
    }

    public LogoutResult logout(String authToken) throws Exception {
        LogoutRequest request = new LogoutRequest(authToken);
        String responseJson = sendPostRequest("/session", gson.toJson(request));
        return gson.fromJson(responseJson, LogoutResult.class);
    }

    public StartGameResult createGame(String whiteUser, String blackUser, String gameName) throws Exception {
        StartGameRequest request = new StartGameRequest(whiteUser, blackUser, gameName);
        String responseJson = sendPostRequest("/game", gson.toJson(request));
        return gson.fromJson(responseJson, StartGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        ListGamesRequest request = new ListGamesRequest();
        String responseJson = sendPostRequest("/game", gson.toJson(request));
        return gson.fromJson(responseJson, ListGamesResult.class);
    }

    public JoinGameResult joinGame(String authToken, int gameId, String color) throws Exception {
        JoinGameRequest request = new JoinGameRequest(color, authToken, gameId);
        String responseJson = sendPostRequest("/game", gson.toJson(request));
        return gson.fromJson(responseJson, JoinGameResult.class);
    }
}
