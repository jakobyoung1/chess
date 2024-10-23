package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private final Map<String, AuthData> authTokens;

    public AuthDAO(Map<String, AuthData> authTokens) {
        this.authTokens = authTokens;
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        if (authTokens.containsKey(auth.authToken())) {
            throw new DataAccessException("Auth token already exists");
        }
        authTokens.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = authTokens.get(authToken);
        if (auth == null) {
            return null;
        }
        return auth;
    }

    public Map<String, AuthData> getAuthTokens() {
        return authTokens;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        authTokens.remove(authToken);
    }

    public void clear() throws DataAccessException {
        authTokens.clear();
    }
}
