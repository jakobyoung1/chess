package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;  // Clears all auth tokens (optional for testing)
}
