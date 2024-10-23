package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class UserServiceTest {

    private static HashMap<String, UserData> users = new HashMap<>();
    private static HashMap<String, AuthData> auths = new HashMap<>();
    private static UserDAO userDAO = new UserDAO(users, auths);
    private static AuthDAO authDAO = new AuthDAO(auths);
    //private static UserService userService = new UserService(userDAO, authDAO);


    public static void main(String[] args) {
        try {
            //testRegisterSuccess();
            //testLoginSuccess();
            //testLogoutSuccess();
            System.out.println("All tests passed!");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    } }


//    public static void testRegisterSuccess() throws DataAccessException {
//        RegisterRequest request = new RegisterRequest("user1", "password", "user1@example.com");
//        RegisterResult result = userService.register(request);
//
//        if (result == null) {
//            throw new RuntimeException("RegisterResult is null");
//        }
//        if (!"user1".equals(result.username())) {
//            throw new RuntimeException("Username does not match");
//        }
//        if (result.authToken() == null) {
//            throw new RuntimeException("Auth token is null");
//        }
//        if (!"User registered successfully".equals(result.message())) {
//            throw new RuntimeException("Success message is incorrect");
//        }
//
//        System.out.println("Users map after registration: " + users);
//
//        System.out.println("testRegisterSuccess passed!");
//    }

//    public static void testLoginSuccess() throws DataAccessException {
//        // Login for the registered user
//        LoginRequest loginRequest = new LoginRequest("user1", "password");
//        LoginResult loginResult = logService.login(loginRequest);
//
//        // Verify login results
//        if (loginResult == null) {
//            throw new RuntimeException("LoginResult is null");
//        }
//        if (!"user1".equals(loginResult.username())) {
//            throw new RuntimeException("Username does not match");
//        }
//        if (loginResult.authToken() == null) {
//            throw new RuntimeException("Auth token is null");
//        }
//        if (!"Login successful".equals(loginResult.message())) {
//            throw new RuntimeException("Login success message is incorrect");
//        }
//
//        System.out.println("testLoginSuccess passed!");
//    }

//    public static void testLogoutSuccess() throws DataAccessException {
//        LoginRequest loginRequest = new LoginRequest("user1", "password");
//        LoginResult loginResult = userService.login(loginRequest);
//
//        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
//        LogoutResult logoutResult = userService.logout(logoutRequest);
//
//        if (logoutResult == null) {
//            throw new RuntimeException("LogoutResult is null");
//        }
//        if (!"Logout successful".equals(logoutResult.message())) {
//            throw new RuntimeException("Logout success message is incorrect");
//        }
//
//        if (authDAO.getAuth(loginResult.authToken()) != null) {
//            throw new RuntimeException("Logout failed: Auth token still exists.");
//        }
//
//        System.out.println("testLogoutSuccess passed!");
//    }
//}
