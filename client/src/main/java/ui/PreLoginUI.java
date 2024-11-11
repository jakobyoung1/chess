
package ui;

import dataaccess.DataAccessException;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.results.LoginResult;
import server.results.RegisterResult;
import server.service.*;

import java.util.Objects;
import java.util.Scanner;

public class PreLoginUI {
    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final Scanner scanner;
    private String authToken;

    public PreLoginUI(LoginService loginService, RegistrationService registrationService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.scanner = new Scanner(System.in);
        this.authToken = null;
    }

    public void display() {
        while (authToken == null) { // Continue until successfully logged in
            System.out.println("\nAvailable commands: Help, Quit, Login, Register");
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    showHelp();
                    break;
                case "quit":
                    System.exit(0);
                    break;
                case "login":
                    login();
                    break;
                case "register":
                    register();
                    break;
                default:
                    System.out.println("Invalid command. Type 'Help' for options.");
                    break;
            }
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    private void showHelp() {
        System.out.println("Help - Lists available commands.");
        System.out.println("Quit - Exits the program.");
        System.out.println("Login - Log in with your username and password.");
        System.out.println("Register - Register a new account.");
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = loginService.login(request);

            if (Objects.equals(result.message(), "Login successful")) {
                System.out.println("Login successful.");
                authToken = result.authToken(); // Set auth token upon success
            } else {
                System.out.println("Login failed: " + result.message());
            }
        } catch (Exception e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
    }

    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        try {
            RegisterRequest request = new RegisterRequest(username, password, email);
            RegisterResult result = registrationService.register(request);

            if (Objects.equals(result.message(), "User registered successfully"))  {
                System.out.println("Registration successful.");
                authToken = result.authToken();
            } else {
                System.out.println("Registration failed: " + result.message());
            }
        } catch (Exception e) {
            System.out.println("Error registering: " + e.getMessage());
        }
    }
}
