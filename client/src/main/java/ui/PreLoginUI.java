package ui;

import client.ServerFacade;
import server.results.LoginResult;
import server.results.RegisterResult;

import java.util.Objects;
import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private String authToken;
    private String user;

    public PreLoginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.authToken = null;
        this.user = null;
    }

    public void display() {
        while (authToken == null) { // Continue until successfully logged in
            System.out.println("\nAvailable commands: Help, Quit, Login, Register");
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> showHelp();
                case "quit" -> System.exit(0);
                case "login" -> login();
                case "register" -> register();
                default -> System.out.println("Invalid command. Type 'Help' for options.");
            }
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return user;
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
            LoginResult result = serverFacade.logIn(username, password);

            if (Objects.equals(result.message(), "Login successful")) {
                System.out.println("Login successful.");
                user = username;
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
            RegisterResult result = serverFacade.register(username, password, email);

            if (Objects.equals(result.message(), "User registered successfully")) {
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
