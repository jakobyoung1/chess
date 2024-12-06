package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebSocketFacade;
import model.GameData;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.RedrawBoardCommand;
import websocket.commands.ResignCommand;

import java.io.IOException;
import java.util.Scanner;

public class GamePlayUI {

    private final WebSocketFacade webSocketClient;
    private final ChessBoardUI chessBoardUI;
    private final Scanner scanner;
    private final String authToken;
    private final ServerFacade server;
    private GameData gameData;
    private ChessGame.TeamColor color;

    public GamePlayUI(WebSocketFacade webSocketClient, String authToken, ServerFacade server) {
        this.webSocketClient = webSocketClient;
        this.chessBoardUI = new ChessBoardUI();
        this.scanner = new Scanner(System.in);
        this.authToken = authToken;
        this.server = server;
    }

    public void display(GameData gameData, ChessGame.TeamColor color) throws Exception {
        this.gameData = gameData;
        this.color = color;
        boolean inGame = true;
        while (inGame) {
            System.out.print("Enter command (Make Move, Resign, Leave, Highlight Legal Moves, Redraw Board, Help): \n");
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
                case "highlight legal moves" -> {
                    highlightLegalMoves();
                }
                case "redraw board" -> {
                    redrawBoard();
                }
                case "help" -> showHelp();
                default -> System.out.println("Invalid command. Type 'Help' for options.");
            }
        }
    }

    public String redrawBoard() throws Exception {
        RedrawBoardCommand command = new RedrawBoardCommand(authToken, gameData.getGameId());
        webSocketClient.sendCommand(command);
        chessBoardUI.displayGame(gameData, color, null, server); // Clear highlights and redraw
        return "";
    }

    public String drawFromLoad(GameData gameData) throws Exception {
        this.gameData = gameData;
        chessBoardUI.displayGame(gameData,color,null,server);
        return "";
    }

    private void highlightLegalMoves() throws Exception {
        System.out.print("\nEnter the position to highlight legal moves (row and column separated by a space, e.g., '2 3'): ");
        String input = scanner.nextLine();
        String[] position = input.split(" ");
        if (position.length != 2) {
            System.out.println("Invalid input. Please enter row and column as two numbers separated by a space.");
            return;
        }

        try {
            int col = Integer.parseInt(position[0]);
            int row = Integer.parseInt(position[1]);
            ChessPosition pos = new ChessPosition(row, col);

            System.out.println("\nFetching game data...");
            var gameID = gameData.getGameId();
            var games = server.listGames();
            gameData = games.get(gameID-1);
            if (gameData == null) {
                System.out.println("Game not found.");
                return;
            }

            chessBoardUI.displayGame(gameData, color, pos, server);
            System.out.println("Legal moves highlighted for the position (" + row + ", " + col + ").");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Row and column must be numbers.");
        } catch (Exception e) {
            System.out.println("An error occurred while highlighting legal moves: " + e.getMessage());
        }
    }

    private void makeMove(int gameId) {
        System.out.print("Enter start position (e.g., 'e2'): ");
        String start = scanner.nextLine().trim();
        int scol = -1;
        int srow = -1;
        int ecol = -1;
        int erow = -1;

        // Validate and process start position
        if (start.matches("[a-hA-H][1-8]")) { // Match letter for column and digit for row
            scol = letterToNumber(start.charAt(0)); // Convert column letter to number
            srow = Character.getNumericValue(start.charAt(1));
        } else {
            System.out.println("Invalid input. Please enter a position like 'e2'.");
            return;
        }

        System.out.print("Enter end position (e.g., 'e4'): ");
        String end = scanner.nextLine().trim();

        // Validate and process end position
        if (end.matches("[a-hA-H][1-8]")) { // Match letter for column and digit for row
            ecol = letterToNumber(end.charAt(0)); // Convert column letter to number
            erow = Character.getNumericValue(end.charAt(1));
        } else {
            System.out.println("Invalid input. Please enter a position like 'e4'.");
            return;
        }

        try {
            // Create and send the move command
            var move = new ChessMove(new ChessPosition(srow, scol), new ChessPosition(erow, ecol), null);
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameId, move);
            webSocketClient.sendCommand(command);

            // Fetch the updated game data
            var games = server.listGames();
            for (GameData data : games) {
                if (data.getGameId() == gameId) {
                    gameData = data; // Update gameData with the latest state
                    break;
                }
            }

            // Display the updated board
            chessBoardUI.displayGame(gameData, color, null, server);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Row and column must be valid numbers.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid move format. Please try again.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    // Helper method to convert column letters to numbers
    private int letterToNumber(char letter) {
        letter = Character.toLowerCase(letter); // Normalize to lowercase
        return letter - 'a' + 1; // Convert 'a' -> 1, 'b' -> 2, ..., 'h' -> 8
    }

    private void resign(int gameId) throws IOException {
        ResignCommand command = new ResignCommand(authToken, gameId);
        webSocketClient.sendCommand(command);
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
                Highlight Legal Moves - Show legal moves of a piece.
                Redraw board - redraw the board.
                Help - Show this help text.""");
    }
}