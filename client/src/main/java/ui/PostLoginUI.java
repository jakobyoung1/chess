package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.WebSocketFacade;
import model.GameData;

import java.util.List;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade serverFacade;
    private final String authToken;
    private final Scanner scanner;
    private final ChessBoardUI chessBoardUI;
    private final String username;
    private WebSocketFacade webSocketClient;
    private WebSocketFacade ws;
    boolean inGame = false;

    public PostLoginUI(ServerFacade serverFacade, String authToken, String username) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
        this.scanner = new Scanner(System.in);
        this.chessBoardUI = new ChessBoardUI();
        this.username = username;
    }

    public void display() {
        boolean isLoggedIn = true;

        while (isLoggedIn && !inGame) {
            System.out.println("\nAvailable commands: Help, Logout, Create Game, List Games, Play Game, Observe Game");
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> showHelp();
                case "logout" -> isLoggedIn = !logout();
                case "create game" -> createGame();
                case "list games" -> listGames();
                case "play game" -> playGame();
                case "observe game" -> observeGame();
                default -> System.out.println("Invalid command. Type 'Help' for options.");
            }
        }
    }

    private void showHelp() {
        System.out.println("""
                Post-login commands:
                  Help - Shows this help text.
                  Logout - Logs out of the system.
                  Create Game - Starts a new game.
                  List Games - Lists all available games.
                  Play Game - Allows you to join a game.
                  Observe Game - Allows you to watch a game in progress.
                """);
    }

    private boolean logout() {
        try {
            serverFacade.logout();
            disconnectWebSocket();
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
            String colorInput = scanner.nextLine().toUpperCase();

            ChessGame.TeamColor color;
            if (colorInput.equals("WHITE")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (colorInput.equals("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                System.out.println("Invalid color. Please enter WHITE or BLACK.");
                return;
            }

            var gameData = games.get(gameNumber - 1);
            serverFacade.joinGame(gameData.getGameId(), username, color.name());
            games = serverFacade.listGames();
            gameData = games.get(gameNumber - 1);
            System.out.println("Joined game: " + gameData.getGameName() + " as " + color);
            ws = new WebSocketFacade("http://localhost:8080");
            ws.joinGame(authToken, gameData.getGameId(), color);
            GamePlayUI gamePlayUI = new GamePlayUI(ws, authToken, serverFacade);
            inGame = true;
            gamePlayUI.display(gameData, color);
            inGame = false;


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

            ws = new WebSocketFacade("http://localhost:8080");
            ws.joinGame(authToken, gameData.getGameId(), null);
            GamePlayUI gamePlayUI = new GamePlayUI(ws, authToken, serverFacade);
            inGame = true;
            gamePlayUI.display(gameData, null);
            inGame = false;



        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error observing game: " + e.getMessage());
        }
    }

    private void disconnectWebSocket() throws Exception {
        if (webSocketClient != null) {
            webSocketClient.session.close();
            webSocketClient = null;
        }
    }
}