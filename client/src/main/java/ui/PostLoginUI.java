package ui;

import server.requests.JoinGameRequest;
import server.requests.ListGamesRequest;
import server.requests.LogoutRequest;
import server.requests.StartGameRequest;
import server.results.StartGameResult;
import server.service.*;
import server.Server;
import server.results.ListGamesResult;
import server.results.JoinGameResult;
import server.results.LogoutResult;

import java.util.Objects;
import java.util.Scanner;

public class PostLoginUI {
    private final Server server;
    private final LogoutService logoutService;
    private final StartGameService startGameService;
    private final ListGamesService listGamesService;
    private final JoinGameService joinGameService;
    private final String authToken;
    private final Scanner scanner;

    public PostLoginUI(Server server, String authToken, LogoutService logoutService,
                       StartGameService startGameService, ListGamesService listGamesService,
                       JoinGameService joinGameService) {
        this.server = server;
        this.authToken = authToken;
        this.logoutService = logoutService;
        this.startGameService = startGameService;
        this.listGamesService = listGamesService;
        this.joinGameService = joinGameService;
        this.scanner = new Scanner(System.in);
    }

    public void display() {
        boolean isLoggedIn = true;

        while (isLoggedIn) {
            System.out.println("\nAvailable commands: Help, Logout, Create Game, List Games, Play Game, Observe Game");
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    showHelp();
                    break;
                case "logout":
                    isLoggedIn = !logout();
                    break;
                case "create game":
                    createGame();
                    break;
                case "list games":
                    listGames();
                    break;
                case "play game":
                    playGame();
                    break;
                case "observe game":
                    observeGame();
                    break;
                default:
                    System.out.println("Invalid command. Type 'Help' for options.");
            }
        }
    }

    private void showHelp() {
        System.out.println("\nPost-login commands:");
        System.out.println("  Help - Shows this help text.");
        System.out.println("  Logout - Logs out of the system.");
        System.out.println("  Create Game - Starts a new game.");
        System.out.println("  List Games - Lists all available games.");
        System.out.println("  Play Game - Allows you to join a game.");
        System.out.println("  Observe Game - Allows you to watch a game in progress.");
    }

    private boolean logout() {
        try {
            LogoutRequest request = new LogoutRequest(authToken);
            LogoutResult result = logoutService.logout(request);
            if (Objects.equals(result.message(), "Logout successful")) {
                System.out.println("Successfully logged out.");
                return true;
            } else {
                System.out.println("Logout failed: " + result.message());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            return false;
        }
    }

    private void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        try {
            StartGameRequest request = new StartGameRequest(null, null, gameName);
            StartGameResult result = startGameService.startGame(request);

            if (result != null && result.getMessage().equals("Game created successfully")) {
                System.out.println(result.getMessage());
            } else {
                System.out.println("Error creating game: " + (result != null ? result.getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("Error during game creation: " + e.getMessage());
        }
    }


}
