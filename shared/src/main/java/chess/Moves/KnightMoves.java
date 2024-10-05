package chess.Moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves {

    public static Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {

        ArrayList<ChessMove> moves = new ArrayList<>();
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
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != piece.getTeamColor()) {
                    ChessMove newmove = new ChessMove(myPosition, newPosition, null);
                    if (ChessGame.wouldBeInCheck(piece.getTeamColor(), newmove, board)) {
                        // do nothing
                    } else {
                        moves.add(newmove);
                    }
                }
            }
        }
        return moves;
    }
}
