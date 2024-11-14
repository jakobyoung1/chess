package client;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import requests.JoinGameRequest;
import requests.LogoutRequest;
import results.*;

import java.io.*;
import java.net.*;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(int urlPort) {
        String url = "http://localhost:";
        url += String.valueOf(urlPort);
        serverUrl = url;
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        var path = "/user";
        UserData user = new UserData(username, password, email);
        RegisterResult res = this.makeRequest("POST", path, user, RegisterResult.class);
        authToken = res.authToken();
        return res;
    }

    public LoginResult logIn(String username, String password) throws Exception{
        var path = "/session";
        UserData user = new UserData(username, password, null);
        LoginResult res = this.makeRequest("POST", path, user, LoginResult.class);
        authToken = res.authToken();
        return res;
    }

    public LogoutResult logout() throws Exception {
        var path = "/session";
        System.out.println("about to make a request");
        LogoutRequest req = new LogoutRequest(authToken);
        LogoutResult res = this.makeRequest("DELETE", path, req, LogoutResult.class);
        System.out.println("made a request");
        return res;
    }

    public StartGameResult createGame(String name) throws Exception {
        var path = "/game";
        GameData game = new GameData(0, null, null, name);
        StartGameResult res = this.makeRequest("POST", path, game, StartGameResult.class);
        return res;
    }

    public JoinGameResult joinGame(int gameID, String username, String color) throws Exception {
        var path = "/game";
        var req = new JoinGameRequest(color, username, gameID);
        JoinGameResult res = this.makeRequest("PUT", path, req, JoinGameResult.class);
        return res;
    }

    public List<GameData> listGames() throws Exception {
        var path = "/game";
        ListGamesResult res = this.makeRequest("GET", path, null, ListGamesResult.class);
        return res.getGames();
    }

    public void clearDB() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                System.out.println("Authorization header: " + authToken);
                http.addRequestProperty("Authorization", authToken);
            }
            if (request != null){
                http.setDoOutput(true);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            var res = readBody(http, responseClass);
            return res;
        } catch (Exception ex) {

            throw new Exception(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception("Error: Not successful");
        }
    }


    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);

                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}