package results;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class ListGamesResult {
    private final List<GameData> games;
    private final String message;

    public ListGamesResult(List<GameData> games) {
        this.games = games;
        this.message = "List retrieved successfully";
    }

    public ListGamesResult(String message) {
        this.games = null;
        this.message = message;
    }

    public List<GameData> getGames() {
        return games;
    }

    public String getMessage() {
        return message;
    }

}
