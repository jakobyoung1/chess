package chess;

import chess.Moves.*;

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
            return KingMoves.getKingMoves(board, myPosition, this);
        }


        //KNIGHT MOVEMENT
        if (this.type == PieceType.KNIGHT) {
            return KnightMoves.getKnightMoves(board, myPosition, this);
        }

        //PAWN MOVEMENT
        if (this.type == PieceType.PAWN) {
            return PawnMoves.getPawnMoves(board, myPosition, this);
        }

        //ROOK MOVEMENT
        if (this.type == PieceType.ROOK) {
            return RookMoves.getRookMoves(board, myPosition, this);
        }

        //QUEEN MOVEMENT
        if (this.type == PieceType.QUEEN) {
            return QueenMoves.getQueenMoves(board, myPosition, this);
        }

        //BISHOP MOVEMENT
        if (this.type == PieceType.BISHOP) {
            return BishopMoves.getBishopMoves(board, myPosition, this);
        }

        return null;
    }

    public String toString() {
        switch (pieceColor) {
            case WHITE:
                switch (type) {
                    case KING:
                        return "K";
                    case QUEEN:
                        return "Q";
                    case BISHOP:
                        return "B";
                    case KNIGHT:
                        return "N";
                    case ROOK:
                        return "R";
                    case PAWN:
                        return "P";
                    default:
                        return "";
                }
            case BLACK:
                switch (type) {
                    case KING:
                        return "k";
                    case QUEEN:
                        return "q";
                    case BISHOP:
                        return "b";
                    case KNIGHT:
                        return "n";
                    case ROOK:
                        return "r";
                    case PAWN:
                        return "p";
                    default:
                        return "";
                }
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
