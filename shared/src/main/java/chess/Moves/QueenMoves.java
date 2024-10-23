package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves {
    public static Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        //horizontal and diagonal
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}};

        return LinearMoves.getLinearMoves(directions, myPosition, piece, board);
    }
}
