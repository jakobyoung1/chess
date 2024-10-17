package dataaccess;

import model.AuthData;
import java.sql.*;

public class AuthDAO {
    private final Connection connection;

    public AuthDAO(Connection connection) {
        this.connection = connection;
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO Auth (authToken, username) VALUES (?, ?)";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, auth.authToken());
            s.setString(2, auth.username());
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not create new Auth", e);
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM Auth WHERE authToken = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, authToken);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve Auth", e);
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM Auth WHERE authToken = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, authToken);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete auth", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Auth";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not clear auth data", e);
        }
    }
}
