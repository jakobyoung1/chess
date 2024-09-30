package chess.Moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {
    public static Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        //horizontal
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] direction : directions) {

            int nRow = myPosition.getRow();
            int nCol = myPosition.getColumn();

            while (board.inBounds(nRow + direction[0], nCol + direction[1])) {
                nRow += direction[0];
                nCol += direction[1];

                ChessPosition newPosition = new ChessPosition(nRow, nCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (pieceAtNewPosition.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }
}