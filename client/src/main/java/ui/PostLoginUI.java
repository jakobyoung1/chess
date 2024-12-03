package ui;

import client.ServerFacade;
import com.google.gson.Gson;
import model.GameData;
import websocket.WebSocketClient;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.List;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade serverFacade;
    private final String authToken;
    private final Scanner scanner;
    private final ChessBoardUI chessBoardUI;
    private final String username;
    private WebSocketClient webSocketClient;

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
            String color = scanner.nextLine().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please enter WHITE or BLACK.");
                return;
            }

            var gameData = games.get(gameNumber - 1);
            serverFacade.joinGame(gameData.getGameId(), username, color);
            System.out.println("Joined game: " + gameData.getGameName() + " as " + color);

            setupWebSocketConnection(gameData.getGameId());
            startGameplay(gameData);

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

            setupWebSocketConnection(gameData.getGameId());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error observing game: " + e.getMessage());
        }
    }

    private void setupWebSocketConnection(int gameId) throws Exception {
        // Check if the WebSocket client is already initialized
        if (webSocketClient == null) {
            webSocketClient = new WebSocketClient();
        }

        // Connect to the WebSocket server
        System.out.println("Connecting to WebSocket server...");
        webSocketClient.connect("ws://localhost:8080/ws");

        // Send a ConnectCommand to the server
        ConnectCommand connectCommand = new ConnectCommand(authToken, gameId);
        String connectMessage = new Gson().toJson(connectCommand);
        System.out.println("Sending ConnectCommand: " + connectMessage);
        webSocketClient.sendMessage(connectMessage);

        // Set up a handler for incoming messages
        webSocketClient.setMessageHandler(rawMessage -> {
            System.out.println("Received message: " + rawMessage);
            try {
                // Parse the raw message into a generic ServerMessage
                ServerMessage serverMessage = new Gson().fromJson(rawMessage, ServerMessage.class);

                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        // Parse and handle a LoadGameMessage
                        LoadGameMessage loadGameMessage = new Gson().fromJson(rawMessage, LoadGameMessage.class);
                        System.out.println("Game loaded successfully.");
                        chessBoardUI.displayBoard(loadGameMessage.getGame());
                    }
                    case NOTIFICATION -> {
                        // Parse and handle a NotificationMessage
                        NotificationMessage notificationMessage = new Gson().fromJson(rawMessage, NotificationMessage.class);
                        System.out.println("Notification: " + notificationMessage.getNotificationMessage());
                    }
                    case ERROR -> {
                        // Parse and handle an ErrorMessage
                        ErrorMessage errorMessage = new Gson().fromJson(rawMessage, ErrorMessage.class);
                        System.err.println("Error: " + errorMessage.getMessage());
                    }
                    default -> {
                        // Handle unknown message types
                        System.err.println("Unknown message type received: " + serverMessage.getServerMessageType());
                    }
                }
            } catch (Exception e) {
                // Log any issues with message handling
                System.err.println("Error processing WebSocket message: " + rawMessage);
                e.printStackTrace();
            }
        });
    }

    private void disconnectWebSocket() throws Exception {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            webSocketClient = null;
        }
    }

    private void startGameplay(GameData gameData) {
        GamePlayUI gameplayUI = new GamePlayUI(webSocketClient);
        gameplayUI.display(gameData);
    }
}
