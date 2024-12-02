package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;


public class LinearMoves {
    public static ArrayList<ChessMove> getLinearMoves(int[][] directions, ChessPosition pos, ChessPiece piece, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions) {
            int nRow = pos.getRow();
            int nCol = pos.getColumn();

            while (board.inBounds(nRow + direction[0], nCol + direction[1])) {
                nRow += direction[0];
                nCol += direction[1];

                ChessPosition newPosition = new ChessPosition(nRow, nCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    ChessMove newmove = new ChessMove(pos, newPosition, null);
                    //if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                    // do nothing
                    //} else {
                    moves.add(newmove);
                    //}
                } else {
                    if (pieceAtNewPosition.getTeamColor() != piece.getTeamColor()) {
                        ChessMove newmove = new ChessMove(pos, newPosition, null);
                        //if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                        // do nothing
                        //} else {
                        moves.add(newmove);
                        //}
                    }
                    break;
                }
            }
        }
        return moves;
    }
}
