package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private static final Gson GSON = new Gson();

    public GameDAO() {
    }

    public void createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Game (game_id, white_username, black_username, game_name, game_state) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, game.getGameId());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameName());
            stmt.setString(5, GSON.toJson(game.getGame()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }
    }

    public GameData getGame(int gameId) throws DataAccessException {
        String sql = "SELECT * FROM Game WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String whiteUsername = rs.getString("white_username");
                String blackUsername = rs.getString("black_username");
                String gameName = rs.getString("game_name");
                String gameStateJson = rs.getString("game_state");

                GameData game = new GameData(gameId, whiteUsername, blackUsername, gameName);
                ChessGame deserializedGame = GSON.fromJson(gameStateJson, ChessGame.class); // Deserialize here
                game.setGame(deserializedGame);
                return game;
            }

            throw new DataAccessException("Game not found");

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM Game";
        List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                String whiteUsername = rs.getString("white_username");
                String blackUsername = rs.getString("black_username");
                String gameName = rs.getString("game_name");
                String gameStateJson = rs.getString("game_state");

                GameData game = new GameData(gameId, whiteUsername, blackUsername, gameName);
                ChessGame deserializedGame = GSON.fromJson(gameStateJson, ChessGame.class);
                game.setGame(deserializedGame);
                games.add(game);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return games;
    }

    public void updateGame(int gameId, GameData game) throws DataAccessException {
        String sql = "UPDATE Game SET white_username = ?, black_username = ?, game_name = ?, game_state = ? WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            stmt.setString(4, GSON.toJson(game.getGame()));
            stmt.setInt(5, gameId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Game not found");
            }

            System.out.println("Game updated in database: " + gameId);

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            System.out.println("Game table cleared in database");

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing Game table: " + e.getMessage());
        }
    }
}
