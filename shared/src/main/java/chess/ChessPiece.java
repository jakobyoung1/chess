package chess;

import chess.Moves.ChessMove;
import chess.Moves.KingMoves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;
import static java.lang.Math.abs;

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

        //KING MOVEMENT
        if (this.type == PieceType.KING) {
            ArrayList<ChessMove> moves = new ArrayList<>();
            // Corrected directions array matching the expected output order
            int[][] directions = {
                    {1, -1},
                    {0, 1},
                    {1, 0},
                    {-1, 0},
                    {1, 1},
                    {0, -1},
                    {-1, 1},
                    {-1, -1}
            };
            for (int[] direction : directions) {
                int nRow = myPosition.getRow() + direction[0];
                int nCol = myPosition.getColumn() + direction[1];

                if (board.inBounds(nRow, nCol)) {
                    ChessPosition newPosition = new ChessPosition(nRow, nCol);
                    ChessPiece piece = board.getPiece(newPosition);
                    if (piece == null || piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            return moves;
        }


        //KNIGHT MOVEMENT
        if (this.type == PieceType.KNIGHT) {
            HashSet<ChessMove> moves = new HashSet<>();
            // Corrected directions array matching the expected output order
            int[][] directions = {
                    {2, 1},
                    {2, -1},
                    {1, 2},
                    {1, -2},
                    {-1, 2},
                    {-1, -2},
                    {-2, 1},
                    {-2, -1}
            };
            for (int[] direction : directions) {
                int nRow = myPosition.getRow() + direction[0];
                int nCol = myPosition.getColumn() + direction[1];

                if (board.inBounds(nRow, nCol)) {
                    ChessPosition newPosition = new ChessPosition(nRow, nCol);
                    ChessPiece piece = board.getPiece(newPosition);
                    if (piece == null || piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            return moves;
        }

        //PAWN MOVEMENT
        if (this.type == PieceType.PAWN) {
            HashSet<ChessMove> moves = new HashSet<>();
            int [][] directions;
            // Corrected directions array matching the expected output order
            if (this.getTeamColor()==WHITE) {
                directions = new int[][] {
                        {1, 0},
                        {2, 0},
                        {1, 1},
                        {1, -1}
                };
            } else {
                directions = new int[][]{
                        {-1, 0},
                        {-2, 0},
                        {-1, 1},
                        {-1, -1}
                };
            }

            for (int[] direction : directions) {
                int nRow = myPosition.getRow() + direction[0];
                int nCol = myPosition.getColumn() + direction[1];

                if (board.inBounds(nRow, nCol)) {
                    ChessPosition newPosition = new ChessPosition(nRow, nCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    //checking for a piece directly in front of me - stop a jump on initial move
                    ChessPosition piece2pos;
                    if (this.getTeamColor()==BLACK) {
                        piece2pos = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
                    } else {
                        piece2pos = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
                    }
                    ChessPiece piece2 = board.getPiece(piece2pos);

                    if (piece == null) {
                        // if there is not a piece
                        if (myPosition.getColumn() == newPosition.getColumn()) {
                            // if i am just moving forward
                            if (myPosition.getRow() == 2 || myPosition.getRow() == 7) {
                                // if at starting pos
                                if (piece2 == null) {
                                    if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
                                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));

                                    } else {
                                        moves.add(new ChessMove(myPosition, newPosition, null));
                                    }
                                }
                            } else {
                                // if not at starting pos
                                if (abs(myPosition.getRow()-newPosition.getRow()) == 1) {
                                    moves.add(new ChessMove(myPosition, newPosition, null));
                                }
                            }
                        }
                    } else {
                        // if there is a piece
                        //if the piece is not in front of me, attack!
                        if (piece.getTeamColor() != this.getTeamColor() && myPosition.getColumn() != newPosition.getColumn()) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        } // else dont move at all
                    }
                }
            }
            return moves;
        }









        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
