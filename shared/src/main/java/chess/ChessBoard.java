package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final int width = 8;
    private final int height = 8;
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (inBounds(position.getRow(), position.getColumn())) {
            squares[position.getRow()-1][position.getColumn()-1] = piece;

        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (inBounds(position.getRow(), position.getColumn())) {
            return squares[position.getRow()-1][position.getColumn()-1];
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        System.out.println("Working on board reset");

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                squares[row][column] = null;
                if (row == 0) {
                    switch (column) {
                        case 0:
                            squares[row][column] = new ChessPiece(WHITE, ROOK);
                        case 1:
                            squares[row][column] = new ChessPiece(WHITE, KNIGHT);
                        case 2:
                            squares[row][column] = new ChessPiece(WHITE, BISHOP);
                        case 3:
                            squares[row][column] = new ChessPiece(WHITE, QUEEN);
                        case 4:
                            squares[row][column] = new ChessPiece(WHITE, KING);
                        case 5:
                            squares[row][column] = new ChessPiece(WHITE, BISHOP);
                        case 6:
                            squares[row][column] = new ChessPiece(WHITE, KNIGHT);
                        case 7:
                            squares[row][column] = new ChessPiece(WHITE, ROOK);
                    }
                } else if (row == 1) {
                    squares[row][column] = new ChessPiece(WHITE, PAWN);
                } if (row == 7) {
                    switch (column) {
                        case 0:
                            squares[row][column] = new ChessPiece(WHITE, ROOK);
                        case 1:
                            squares[row][column] = new ChessPiece(WHITE, KNIGHT);
                        case 2:
                            squares[row][column] = new ChessPiece(WHITE, BISHOP);
                        case 3:
                            squares[row][column] = new ChessPiece(WHITE, QUEEN);
                        case 4:
                            squares[row][column] = new ChessPiece(WHITE, KING);
                        case 5:
                            squares[row][column] = new ChessPiece(WHITE, BISHOP);
                        case 6:
                            squares[row][column] = new ChessPiece(WHITE, KNIGHT);
                        case 7:
                            squares[row][column] = new ChessPiece(WHITE, ROOK);
                    }
                } else if (row == 6) {
                    squares[row][column] = new ChessPiece(WHITE, PAWN);
                }

            }
        }



    }

    public boolean inBounds(int row, int col) {
        row = row-1;
        col = col-1;
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return false;
        }

        return true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return width == that.width && height == that.height && Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, Arrays.deepHashCode(squares));
    }
}
