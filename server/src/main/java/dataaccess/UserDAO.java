package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class UserDAO implements UserDataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Object, Object> authTokenMap = new HashMap<>();

    public UserDAO(Connection connection) {
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }
        return user;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    public void invalidateSession(String authToken) throws DataAccessException {

        if (authTokenMap.containsKey(authToken)) {
            authTokenMap.remove(authToken);  // Remove the token, invalidating the session
        } else {
            throw new DataAccessException("Auth token not found");  // Token not found
        }
    }

}
