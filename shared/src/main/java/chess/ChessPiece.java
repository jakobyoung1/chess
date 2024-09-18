package chess;

import chess.Moves.ChessMove;
import chess.Moves.KingMoves;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.KING;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (this.type == PieceType.KING) {
            ArrayList<ChessMove> moves = new ArrayList<>();
            // Corrected directions array matching the expected output order
            int[][] directions = {   // [2,6]  (Top-right)
                    {1, -1},   // [4,5]  (Bottom-left)
                    {0, 1},    // [3,7]  (Right)
                    {1, 0},    // [4,6]  (Bottom)
                    {-1, 0},   // [2,7]  (Up)
                    {1, 1},    // [4,7]  (Bottom-right)
                    {0, -1},
                    {-1, 1},   // [3,5]  (Left)
                    {-1, -1}   // [2,5]  (Top-left)
            };
            for (int[] direction : directions) {
                int nRow = myPosition.getRow() + direction[0];
                int nCol = myPosition.getColumn() + direction[1];

                if (board.inBounds(nRow, nCol)) {
                    ChessPosition newPosition = new ChessPosition(nRow, nCol);
                    ChessPiece piece = board.getPiece(newPosition);
                    if (piece == null || piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, this.getPieceType()));
                    }
                }
            }
            return moves;
        }
        return null;
    }
}
