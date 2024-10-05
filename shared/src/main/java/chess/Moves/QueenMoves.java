package chess.Moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves {
    public static Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        //horizontal and diagonal
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}};

        for (int[] direction : directions) {

            int nRow = myPosition.getRow();
            int nCol = myPosition.getColumn();

            while (board.inBounds(nRow + direction[0], nCol + direction[1])) {
                nRow += direction[0];
                nCol += direction[1];

                ChessPosition newPosition = new ChessPosition(nRow, nCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    ChessMove newmove = new ChessMove(myPosition, newPosition, null);
                    if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                        // do nothing
                    } else {
                        moves.add(newmove);
                    }
                } else {
                    if (pieceAtNewPosition.getTeamColor() != piece.getTeamColor()) {
                        ChessMove newmove = new ChessMove(myPosition, newPosition, null);
                        if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                            // do nothing
                        } else {
                            moves.add(newmove);
                        }
                    }
                    break;
                }
            }
        }
        return moves;
    }
}
