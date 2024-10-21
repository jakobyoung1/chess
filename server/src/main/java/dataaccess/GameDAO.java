package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class GameDAO {
    private Map<Integer, GameData> games = new HashMap<>();

    public GameDAO(HashMap<Integer, GameData> games) {
        this.games = games;
    }

    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.getGameId())) {
            throw new DataAccessException("Game already exists");
        }
        games.put(game.getGameId(), game);
    }

    public GameData getGame(int gameId) throws DataAccessException {
        GameData game = games.get(gameId);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());  // Return all games in the map as a list
    }

    public void updateGame(int gameId, ChessGame updatedGame) throws DataAccessException {
        GameData gameData = games.get(gameId);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        gameData.setGame(updatedGame);
        games.put(gameId, gameData);
    }

    public void deleteGame(int gameId) throws DataAccessException {
        if (!games.containsKey(gameId)) {
            throw new DataAccessException("Game not found");
        }
        games.remove(gameId);
    }

    public void clear() throws DataAccessException {
        games.clear();
    }
}
