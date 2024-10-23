package server.results;

import model.GameData;

public class StartGameResult {
    private final Integer gameID;
    private final GameData gameData;
    private final String message;

    public StartGameResult(Integer gameID, GameData gameData, String message) {
        this.gameID = gameID;
        this.gameData = gameData;
        this.message = message;
    }


    public int getGameId() {
        return gameID;
    }

    public GameData getGameData() {
        return gameData;
    }

    public String getMessage() {
        return message;
    }
}
