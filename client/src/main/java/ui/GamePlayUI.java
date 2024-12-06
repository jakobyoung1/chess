package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebSocketFacade;
import model.GameData;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import java.io.IOException;
import java.util.Scanner;

public class GamePlayUI {

    private final WebSocketFacade webSocketClient;
    private final ChessBoardUI chessBoardUI;
    private final Scanner scanner;
    private final String authToken;
    private final ServerFacade server;

    public GamePlayUI(WebSocketFacade webSocketClient, String authToken, ServerFacade server) {
        this.webSocketClient = webSocketClient;
        this.chessBoardUI = new ChessBoardUI();
        this.scanner = new Scanner(System.in);
        this.authToken = authToken;
        this.server = server;
    }

    public void display(GameData gameData, ChessGame.TeamColor color) throws Exception {
        boolean inGame = true;
        while (inGame) {
            chessBoardUI.displayGame(gameData, color, null, server);
            System.out.print("Enter command (Make Move, Resign, Leave, Help): \n");
            String command = scanner.nextLine();
            switch (command.toLowerCase()) {
                case "make move" -> makeMove(gameData.getGameId());
                case "resign" -> {
                    resign(gameData.getGameId());
                }
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
        int scol = -1;
        int srow = -1;
        int ecol = -1;
        int erow = -1;
        if (start.length() == 2 && Character.isDigit(start.charAt(0)) && Character.isDigit(start.charAt(1))) {
            scol = Character.getNumericValue(start.charAt(0));
            srow = Character.getNumericValue(start.charAt(1));
        } else {
            System.out.println("Invalid input. Please enter a position like 'e2'.");
        }
        System.out.print("Enter end position (e.g., e4): ");
        String end = scanner.nextLine();
        if (end.length() == 2 && Character.isDigit(end.charAt(0)) && Character.isDigit(end.charAt(1))) {
            ecol = Character.getNumericValue(end.charAt(0));
            erow = Character.getNumericValue(end.charAt(1));
        } else {
            System.out.println("Invalid input. Please enter a position like 'e2'.");
        }
        try {
            var move = new ChessMove(new ChessPosition(srow, scol), new ChessPosition(erow, ecol), null);
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameId, move);
            webSocketClient.sendCommand(command);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid move format. Please try again.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(int gameId) throws IOException {
        ResignCommand command = new ResignCommand("authToken", gameId);
        webSocketClient.sendCommand(command);
        System.out.println("You have resigned from the game.");
    }

    private void leaveGame(int gameId) throws IOException {
        LeaveCommand command = new LeaveCommand(authToken, gameId);
        webSocketClient.sendCommand(command);
        System.out.println("You have left the game.");
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