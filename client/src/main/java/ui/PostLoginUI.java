package ui;

import client.ServerFacade;
import server.results.JoinGameResult;
import server.results.ListGamesResult;
import server.results.LogoutResult;
import server.results.StartGameResult;

import java.util.Objects;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade serverFacade;
    private final String authToken;
    private final Scanner scanner;
    private final ChessBoardUI chessBoardUI;
    private final String username;

    public PostLoginUI(ServerFacade serverFacade, String authToken, String username) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
        this.scanner = new Scanner(System.in);
        this.chessBoardUI = new ChessBoardUI();
        this.username = username;
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
            LogoutResult result = serverFacade.logout();
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
            StartGameResult result = serverFacade.createGame(gameName);

            if (result != null && result.getMessage().equals("Game created successfully")) {
                System.out.println(result.getMessage());
            } else {
                System.out.println("Error creating game: " + (result != null ? result.getMessage() : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("Error during game creation: " + e.getMessage());
        }
    }

    private void listGames() {
        System.out.println("listing games\n");
        try {
            ListGamesResult result = serverFacade.listGames();

            if (result != null && !result.getGames().isEmpty()) {
                int index = 1;
                for (var game : result.getGames()) {
                    System.out.printf("%d. %s - White: %s, Black: %s%n", index++, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
                }
            } else {
                System.out.println("No games available.");
            }
        } catch (Exception e) {
            System.out.println("Error listing games: " + e.getMessage());
        }
    }

    private void playGame() {
        System.out.print("Enter game number to play: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter color (WHITE/BLACK): ");
        String color = scanner.nextLine().toUpperCase();

        try {
            JoinGameResult result = serverFacade.joinGame(gameNumber, username, color);

            if (Objects.equals(result.getMessage(), "Joined game successfully")) {
                System.out.println("Joined game successfully.");
                chessBoardUI.displayBoard();
            } else {
                System.out.println("Error joining game: " + result.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error joining game: " + e.getMessage());
        }
    }

    private void observeGame() {
        System.out.print("Enter game number to observe: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());
        chessBoardUI.displayBoard();  // Display the initial state of the board
    }
}
