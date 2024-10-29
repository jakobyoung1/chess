package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO implements UserDataAccess {

    public UserDAO() {
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        System.out.println("Attempting to insert user: " + user.getUsername());

        String sql = "INSERT INTO User (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();

            System.out.println("User inserted into database: " + user.getUsername());

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("Attempting to retrieve user: " + username);

        String sql = "SELECT * FROM User WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                String email = rs.getString("email");
                System.out.println("User found in database: " + username);
                return new UserData(username, passwordHash, email);
            }

            System.out.println("User not found in database: " + username);
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        System.out.println("Clearing User data from database");

        String sql = "DELETE FROM User";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            System.out.println("User table cleared in database");

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing User table: " + e.getMessage());
        }
    }
}
