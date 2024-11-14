package ui;

import client.ServerFacade;
import model.GameData;
import java.util.List;
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
            serverFacade.logout();
            return true;
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            return false;
        }
    }

    private void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        try {
            serverFacade.createGame(gameName);

        } catch (Exception e) {
            System.out.println("Error during game creation: " + e.getMessage());
        }
    }

    private void listGames() {
        System.out.println("listing games\n");
        try {
            List<GameData> games = serverFacade.listGames();

            int index = 1;
            for (var game : games) {
                System.out.printf("%d. %s - White: %s, Black: %s%n", index++, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
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
            serverFacade.joinGame(gameNumber, username, color);
            chessBoardUI.displayBoard();

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
