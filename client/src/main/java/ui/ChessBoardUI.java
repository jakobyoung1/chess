package ui;

public class ChessBoardUI {

    private static final String RESET = "\u001B[0m";
    private static final String WHITE_PIECE = "\u001B[31m";
    private static final String BLACK_PIECE = "\u001B[34m";
    private static final String LIGHT_SQUARE = "\u001B[47m";
    private static final String DARK_SQUARE = "\u001B[100m";

    private static final char[][] INITIAL_BOARD = {
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}
    };

    public void displayBoard() {
        printBoard(INITIAL_BOARD, true);
        printBoard(INITIAL_BOARD, false);
    }

    private void printBoard(char[][] board, boolean whitePerspective) {
        if (!whitePerspective) {
            reverseBoard(board);
        }

        for (int i = 0; i < 8; i++) {
            System.out.print((whitePerspective ? 8 - i : i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                boolean isLightSquare = (i + j) % 2 == 0;
                System.out.print((isLightSquare ? LIGHT_SQUARE : DARK_SQUARE));

                char piece = board[i][j];
                if (piece == ' ') {
                    System.out.print("   ");
                } else if (Character.isUpperCase(piece)) {
                    System.out.print(" " + WHITE_PIECE + piece + " ");
                } else {
                    System.out.print(" " + BLACK_PIECE + Character.toUpperCase(piece) + " ");
                }

                System.out.print(RESET);
            }
            System.out.println(RESET);
        }

        System.out.print("  ");
        for (int j = 0; j < 8; j++) {
            System.out.print(" " + (whitePerspective ? (char) ('a' + j) : (char) ('h' - j)) + " ");
        }
        System.out.println(RESET);
    }

    private void reverseBoard(char[][] board) {
        for (int i = 0; i < 4; i++) {
            char[] temp = board[i];
            board[i] = board[7 - i];
            board[7 - i] = temp;
        }
    }
}
