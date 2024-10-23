package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDAO implements UserDataAccess {
    private Map<String, UserData> users;
    private Map<String, AuthData> auths;

    public UserDAO(HashMap<String, UserData> users, HashMap<String, AuthData> auths) {
        this.users = users;
        this.auths = auths;
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        System.out.println("Attempting to insert user: " + user.getUsername());

        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists");
        }

        users.put(user.getUsername(), user);

        System.out.println("User inserted: " + user.getUsername());
        System.out.println("Users map after insert: " + users);
    }

    public Map<String, UserData> getUsers() {
        return users;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("Attempting to retrieve user: " + username);
        System.out.println("Current users map before retrieval: " + users);

        UserData user = users.get(username);

        if (user == null) {
            return null;
        }

        System.out.println("User found: " + user.getUsername());
        return user;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        auths.clear();
        System.out.println("Users map cleared");
    }
}
