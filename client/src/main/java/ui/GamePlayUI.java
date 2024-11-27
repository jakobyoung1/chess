package ui;

import model.GameData;
import websocket.WebSocketClient;
import websocket.commands.UserGameCommand;

import java.util.Scanner;

public class GamePlayUI {

    private final WebSocketClient webSocketClient;
    private final ChessBoardUI chessBoardUI;
    private final Scanner scanner;

    public GamePlayUI(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        this.chessBoardUI = new ChessBoardUI();
        this.scanner = new Scanner(System.in);
    }

    public void display(GameData gameData) {
        chessBoardUI.displayBoard(gameData);
        while (true) {
            System.out.print("Enter command (Make Move, Resign, Leave, Help): ");
            String command = scanner.nextLine();
            switch (command.toLowerCase()) {
                case "make move" -> makeMove(gameData.getGameId());
                case "resign" -> resign(gameData.getGameId());
                case "leave" -> {
                    leaveGame(gameData.getGameId());
                    return;
                }
                case "help" -> showHelp();
                default -> System.out.println("Invalid command. Type 'Help' for options.");
            }
        }
    }

    private void makeMove(int gameId) {
        System.out.print("Enter start position (e.g., e2): ");
        String start = scanner.nextLine();
        System.out.print("Enter end position (e.g., e4): ");
        String end = scanner.nextLine();

        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "authToken", gameId);
        webSocketClient.sendCommand(command);
    }

    private void resign(int gameId) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, "authToken", gameId);
        webSocketClient.sendCommand(command);
    }

    private void leaveGame(int gameId) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, "authToken", gameId);
        webSocketClient.sendCommand(command);
    }

    private void showHelp() {
        System.out.println("""
                Available commands:
                Make Move - Make a move on the chessboard.
                Resign - Resign from the game.
                Leave - Leave the game.
                Help - Show this help text.""");
    }
}
