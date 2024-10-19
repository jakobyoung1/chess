package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO implements UserDataAccess {
    private final Map<String, UserData> users = new HashMap<>();

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
}
