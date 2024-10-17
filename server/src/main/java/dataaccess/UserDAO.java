package dataaccess;

import model.UserData;
import java.sql.*;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertUser(UserData ud) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, ud.username());
            s.setString(2, ud.password());
            s.setString(3, ud.email());
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not insert user", e);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, username);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not retrieve user", e);
        }
    }

    public void updateUser(UserData ud) throws DataAccessException {
        String sql = "UPDATE Users SET password = ?, email = ? WHERE username = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, ud.password());
            s.setString(2, ud.email());
            s.setString(3, ud.username());
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update user", e);
        }
    }

    public void deleteUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.setString(1, username);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete user", e);
        }
    }

    // Clear (Clear all user data for testing)
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Users";
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing user data", e);
        }
    }
}
