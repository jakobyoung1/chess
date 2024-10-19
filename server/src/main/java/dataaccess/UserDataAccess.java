package dataaccess;

import model.UserData;
import model.AuthData;
import java.util.List;

public interface UserDataAccess {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException; // Clears all users
}
