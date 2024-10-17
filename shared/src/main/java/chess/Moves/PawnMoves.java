package chess.Moves;

import chess.*;
import java.util.HashSet;
import java.util.Collection;
import static java.lang.Math.abs;

public class PawnMoves {

    public static Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] directions;

        // Determine directions based on the pawn's team color
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            directions = new int[][] {
                    {1, 0}, {2, 0}, {1, 1}, {1, -1}
            };
        } else {
            directions = new int[][] {
                    {-1, 0}, {-2, 0}, {-1, 1}, {-1, -1}
            };
        }

        for (int[] direction : directions) {
            int nRow = myPosition.getRow() + direction[0];
            int nCol = myPosition.getColumn() + direction[1];

            if (board.inBounds(nRow, nCol)) {
                ChessPosition newPosition = new ChessPosition(nRow, nCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                // check for piece stopping double move
                ChessPosition piece2pos;
                if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    piece2pos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                } else {
                    piece2pos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                }
                ChessPiece pieceInFront = board.getPiece(piece2pos);

                if (pieceAtNewPosition == null) {
                    // move forward
                    if (myPosition.getColumn() == newPosition.getColumn()) {
                        if (myPosition.getRow() == 2 || myPosition.getRow() == 7) {
                            // at starting pos - allow double move
                            if (pieceInFront == null) {
                                if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
                                    promotePawn(moves, myPosition, newPosition, piece, board);
                                } else {
                                    ChessMove newmove = new ChessMove(myPosition, newPosition, null);
                                    //if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                                        // do nothing
                                    //} else {
                                        moves.add(newmove);
                                    //}
                                }
                            }
                        } else {
                            // move forward
                            if (abs(myPosition.getRow() - newPosition.getRow()) == 1) {
                                if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
                                    promotePawn(moves, myPosition, newPosition, piece, board);
                                } else {
                                    ChessMove newmove = new ChessMove(myPosition, newPosition, null);
                                    //if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                                        // do nothing
                                    //} else {
                                        moves.add(newmove);
                                    //}
                                }
                            }
                        }
                    }
                } else {
                    // there's a piece, attack if diagonal
                    if (pieceAtNewPosition.getTeamColor() != piece.getTeamColor() && myPosition.getColumn() != newPosition.getColumn()) {
                        if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
                            promotePawn(moves, myPosition, newPosition, piece, board);
                        } else {
                            ChessMove newmove = new ChessMove(myPosition, newPosition, null);
                            if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                                // do nothing
                            } else {
                                moves.add(newmove);
                            }
                        }
                    }
                }
            }
        }
        return moves;
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
