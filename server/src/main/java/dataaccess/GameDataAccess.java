package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDataAccess {
    void insertGame(GameData game) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;  // Clears all games
}
