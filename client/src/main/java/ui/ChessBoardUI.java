package ui;

import chess.ChessPiece;
import model.GameData;

public class ChessBoardUI {

    private static final String RESET = "\u001B[0m";
    private static final String WHITE_PIECE = "\u001B[31m";
    private static final String BLACK_PIECE = "\u001B[34m";
    private static final String LIGHT_SQUARE = "\u001B[47m";
    private static final String DARK_SQUARE = "\u001B[100m";

    public void displayBoard(GameData gameData) {
        ChessPiece[][] board = gameData.getGame().getBoard().getSquares();

        System.out.println("White's perspective:");
        printBoard(board, true);

        System.out.println("Black's perspective:");
        printBoard(board, false);
    }

    private void printBoard(ChessPiece[][] board, boolean whitePerspective) {
        for (int i = 0; i < 8; i++) {
            int row = whitePerspective ? 8 - i : i + 1;
            System.out.print(row + " ");

            for (int j = 0; j < 8; j++) {
                int rowIndex = whitePerspective ? i : 7 - i;
                int colIndex = whitePerspective ? j : 7 - j;
                boolean isLightSquare = (rowIndex + colIndex) % 2 == 0;

                System.out.print(isLightSquare ? LIGHT_SQUARE : DARK_SQUARE);

                ChessPiece piece = board[rowIndex][colIndex];
                if (piece == null) {
                    System.out.print("   ");
                } else if (piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE) {
                    System.out.print(" " + WHITE_PIECE + piece.toString() + " ");
                } else {
                    System.out.print(" " + BLACK_PIECE + piece.toString() + " ");
                }

                System.out.print(RESET);
            }
            System.out.println(RESET);
        }

        System.out.print("  ");
        for (int j = 0; j < 8; j++) {
            char col = whitePerspective ? (char) ('a' + j) : (char) ('h' - j);
            System.out.print(" " + col + " ");
        }
        System.out.println(RESET);
    }

}