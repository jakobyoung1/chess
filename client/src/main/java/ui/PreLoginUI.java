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
    private final Scanner scanner = new Scanner(System.in);

    public PreLoginUI(LoginService loginService, RegistrationService registrationService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
    }

    public void display() throws DataAccessException {
        System.out.println("Welcome! Available commands: Help, Quit, Login, Register");
        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    showHelp();
                    break;
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
                    break;
                case "quit":
                    System.out.println("Exiting.");
                    return;
                default:
                    System.out.println("Invalid command. Type 'Help' for options.");
            }
        }
    }

    private void handleLogin() throws DataAccessException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult result = loginService.login(loginRequest);
        if (Objects.equals(result.message(), "Login successful")) {
            System.out.println("Login successful!");
            // Transition to PostLoginUI with authToken
        } else {
            System.out.println("Error logging in: " + result.message());
        }
    }

    private void handleRegister() throws DataAccessException {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        RegisterRequest registrationRequest = new RegisterRequest(username, password, email);
        RegisterResult result = registrationService.register(registrationRequest);
        if (Objects.equals(result.message(), "User registered successfully")) {
            System.out.println("Registration successful!");
            // Transition to PostLoginUI with authToken
        } else {
            System.out.println("Error registering: " + result.message());
        }
    }

    private void showHelp() {
        System.out.println("Available commands: Help, Quit, Login, Register");
    }
}
