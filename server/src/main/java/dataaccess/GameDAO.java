package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private final Connection connection;

    public GameDAO(Connection connection) {
        this.connection = connection;
    }

    public void createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Games (gameID, player1, player2, gameName, ) VALUES (?, ?, ?, ?)";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setInt(1, game.gameID());
            s.setString(2, game.whiteUsername());
            s.setString(3, game.blackUsername());
            s.setString(4, game.gameName());
            s.setObject(5, game.game());
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not add game", e);
        }
    }

    public GameData getGame(String gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameID = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, gameID);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return new GameData(rs.getInt("gameID"), rs.getString("gameState"),
                        rs.getString("player1"), rs.getString("player2"), (ChessGame) rs.getObject("game"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not getGame", e);
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM Games";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                GameData game = new GameData(rs.getInt("gameID"), rs.getString("gameState"),
                        rs.getString("player1"), rs.getString("player2"), (ChessGame) rs.getObject("game"));
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not listGames", e);
        }
        return games;
    }

    public void updateGame(Integer gameID, ChessGame game) throws DataAccessException {
        String sql = "UPDATE Games SET gameState = ? WHERE gameID = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setInt(1, gameID);
            s.setObject(2, game);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not updateGame", e);
        }
    }

    public void deleteGame(String gameID) throws DataAccessException {
        String sql = "DELETE FROM Games WHERE gameID = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, gameID);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete Game", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Games";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not access data", e);
        }
    }
}
