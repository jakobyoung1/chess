package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
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
    private ChessPiece[][] squares = new ChessPiece[height][width];

    private static ChessPosition whiteKing;
    private static ChessPosition blackKing;

    public ChessBoard() {
    }

    public ChessPosition getKingPos(ChessGame.TeamColor color) {
        return switch (color) {
            case WHITE -> whiteKing;
            case BLACK -> blackKing;
            default -> null;
        };
    }

    public void updateKingPosition(ChessGame.TeamColor color) {
        boolean isThereKing = false;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                ChessPiece piece = squares[row][column];
                if (piece != null && piece.getPieceType() == KING && piece.getTeamColor() == color) {
                    if (color == WHITE) {
                        isThereKing = true;
                        whiteKing = new ChessPosition(row + 1, column + 1); // +1 to adjust to 1-based indexing
                    } else if (color == BLACK) {
                        isThereKing = true;
                        blackKing = new ChessPosition(row + 1, column + 1);
                    }
                    return; // Exit once the king is found
                }
            }
        }
        if (!isThereKing) {
            if (color == WHITE) {
                whiteKing = new ChessPosition(99999999, 99999999);
            } else {
                blackKing = new ChessPosition(99999999, 99999999);
            }
        }
        // If the king is not found, you may want to handle this case (e.g., throw an exception or log an error)
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
        for (int row = 7; row >= 0; row--) {
            for (int column = 0; column < 8; column++) {
                squares[row][column] = null;
                if (row == 0) {
                    //System.out.println("FOR ROW: " + row + " COL: " + column + "   ");
                    switch (column) {
                        case 0:
                            squares[row][column] = new ChessPiece(WHITE, ROOK);
                            break;
                        case 1:
                            squares[row][column] = new ChessPiece(WHITE, KNIGHT);
                            break;
                        case 2:
                            squares[row][column] = new ChessPiece(WHITE, BISHOP);
                            break;
                        case 3:
                            squares[row][column] = new ChessPiece(WHITE, QUEEN);
                            break;
                        case 4:
                            squares[row][column] = new ChessPiece(WHITE, KING);
                            whiteKing = new ChessPosition(row, column);
                            break;
                        case 5:
                            squares[row][column] = new ChessPiece(WHITE, BISHOP);
                            break;
                        case 6:
                            squares[row][column] = new ChessPiece(WHITE, KNIGHT);
                            break;
                        case 7:
                            squares[row][column] = new ChessPiece(WHITE, ROOK);
                            break;
                    }
                } else if (row == 1) {
                    squares[row][column] = new ChessPiece(WHITE, PAWN);
                }  else if (row == 6) {
                    squares[row][column] = new ChessPiece(BLACK, PAWN);
                } else if (row == 7) {
                    switch (column) {
                        case 0:
                            squares[row][column] = new ChessPiece(BLACK, ROOK);
                            break;
                        case 1:
                            squares[row][column] = new ChessPiece(BLACK, KNIGHT);
                            break;
                        case 2:
                            squares[row][column] = new ChessPiece(BLACK, BISHOP);
                            break;
                        case 3:
                            squares[row][column] = new ChessPiece(BLACK, QUEEN);
                            break;
                        case 4:
                            squares[row][column] = new ChessPiece(BLACK, KING);
                            blackKing = new ChessPosition(row, column);
                            break;
                        case 5:
                            squares[row][column] = new ChessPiece(BLACK, BISHOP);
                            break;
                        case 6:
                            squares[row][column] = new ChessPiece(BLACK, KNIGHT);
                            break;
                        case 7:
                            squares[row][column] = new ChessPiece(BLACK, ROOK);
                            break;
                    }
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
    public String toString() {
        String output = new String();
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (squares[row][column] != null) {
                    output += squares[row][column].toString() + " ";
                } else {
                    output += " ";
                }
            }
            output += "\n";
        }
        //System.out.println(output);
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return width == that.width && height == that.height && Objects.deepEquals(squares, that.squares);
    }

    public ChessPiece[][] getSquares() {
        return squares;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, Arrays.deepHashCode(squares));
    }
}
