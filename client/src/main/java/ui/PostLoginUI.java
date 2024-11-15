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
            System.out.println("Successfully created game.");
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
        try {
            System.out.print("Enter game number to play: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            var games = serverFacade.listGames();

            if (gameNumber <= 0 || gameNumber > games.size()) {
                System.out.println("Invalid game number. Please try again.");
                return;
            }

            System.out.print("Enter color (WHITE/BLACK): ");
            String color = scanner.nextLine().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please enter WHITE or BLACK.");
                return;
            }

            var gameData = games.get(gameNumber - 1);
            serverFacade.joinGame(gameData.getGameId(), username, color);
            System.out.println("Joined game: " + gameData.getGameName() + " as " + color);

            chessBoardUI.displayBoard(gameData);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error joining game: " + e.getMessage());
        }
    }


    private void observeGame() {
        try {
            System.out.print("Enter game number to observe: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());

            var games = serverFacade.listGames();

            if (gameNumber <= 0 || gameNumber > games.size()) {
                System.out.println("Invalid game number. Please try again.");
                return;
            }

            var gameData = games.get(gameNumber - 1);
            System.out.println("Observing game: " + gameData.getGameName());

            chessBoardUI.displayBoard(gameData);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error observing game: " + e.getMessage());
        }
    }

}
