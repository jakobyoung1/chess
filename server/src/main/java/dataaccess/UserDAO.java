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

        // After inserting the user, print the map
        System.out.println("User inserted: " + user.getUsername());
        System.out.println("Users map after insert: " + users);
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("Attempting to retrieve user: " + username);
        System.out.println("Current users map before retrieval: " + users);

        UserData user = users.get(username);

        if (user == null) {
            return null;
        }

        // If user is found
        System.out.println("User found: " + user.getUsername());
        return user;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        System.out.println("Users map cleared");
    }

    public void invalidateSession(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)) {
            auths.remove(authToken);
        } else {
            throw new DataAccessException("Auth token not found");
        }
    }
}
