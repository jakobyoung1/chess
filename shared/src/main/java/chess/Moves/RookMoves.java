package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {
    public static Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        //horizontal
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        return LinearMoves.getLinearMoves(directions, myPosition, piece, board);
    }
}
