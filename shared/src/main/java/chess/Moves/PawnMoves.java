package chess.moves;

import chess.*;
import java.util.HashSet;
import java.util.Collection;
import static java.lang.Math.abs;

public class PawnMoves {

    public static Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] directions = getPawnDirections(piece);

        for (int[] direction : directions) {
            ChessPosition newPosition = calculateNewPosition(myPosition, direction);
            if (board.inBounds(newPosition.getRow(), newPosition.getColumn())) {
                handleMove(board, myPosition, newPosition, piece, moves);
            }
        }
        return moves;
    }

    private static int[][] getPawnDirections(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return new int[][]{{1, 0}, {2, 0}, {1, 1}, {1, -1}};
        } else {
            return new int[][]{{-1, 0}, {-2, 0}, {-1, 1}, {-1, -1}};
        }
    }

    private static ChessPosition calculateNewPosition(ChessPosition myPosition, int[] direction) {
        int nRow = myPosition.getRow() + direction[0];
        int nCol = myPosition.getColumn() + direction[1];
        return new ChessPosition(nRow, nCol);
    }

    private static void handleMove(ChessBoard board, ChessPosition myPosition,
                                   ChessPosition newPosition, ChessPiece piece,
                                   HashSet<ChessMove> moves) {
        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
        ChessPiece pieceInFront = getPieceInFront(board, myPosition, piece);

        if (pieceAtNewPosition == null) {
            handleForwardMove(board, myPosition, newPosition, piece, moves, pieceInFront);
        } else if (pieceAtNewPosition.getTeamColor() != piece.getTeamColor() && myPosition.getColumn() != newPosition.getColumn()) {
            handleAttackMove(board, myPosition, newPosition, piece, moves);
        }
    }

    private static ChessPiece getPieceInFront(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ChessPosition piece2pos;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            piece2pos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        } else {
            piece2pos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        }
        return board.getPiece(piece2pos);
    }

    private static void handleForwardMove(ChessBoard board,
                                          ChessPosition myPosition,
                                          ChessPosition newPosition,
                                          ChessPiece piece,
                                          HashSet<ChessMove> moves,
                                          ChessPiece pieceInFront) {
        if (myPosition.getColumn() == newPosition.getColumn() && pieceInFront == null) {
            if (isStartingPosition(myPosition)) {
                handleDoubleMove(board, myPosition, newPosition, piece, moves);
            } else if (Math.abs(myPosition.getRow() - newPosition.getRow()) == 1) {
                handleDoubleMove(board, myPosition, newPosition, piece, moves);
            }
        }
    }

    private static boolean isStartingPosition(ChessPosition myPosition) {
        return myPosition.getRow() == 2 || myPosition.getRow() == 7;
    }

    private static void handleDoubleMove(ChessBoard board,
                                         ChessPosition myPosition,
                                         ChessPosition newPosition,
                                         ChessPiece piece,
                                         HashSet<ChessMove> moves) {
        if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
            promotePawn(moves, myPosition, newPosition, piece, board);
        } else {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
    }

    private static void handleAttackMove(ChessBoard board,
                                         ChessPosition myPosition,
                                         ChessPosition newPosition,
                                         ChessPiece piece,
                                         HashSet<ChessMove> moves) {
        if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
            promotePawn(moves, myPosition, newPosition, piece, board);
        } else if (!ChessGame.wouldBeInCheck(piece.getTeamColor(), new ChessMove(myPosition, newPosition, null), board)) {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
    }


    private static void promotePawn(HashSet<ChessMove> moves, ChessPosition from, ChessPosition to, ChessPiece piece, ChessBoard board) {
        ChessMove newmove = new ChessMove(from, to, null);
        if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
            // do nothing
        } else {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        }
    }
}
