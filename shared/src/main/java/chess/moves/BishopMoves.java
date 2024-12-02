package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves {
    public static Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        //diagonal
        int[][] directions = {{-1, 1}, {-1, -1}, {1, 1}, {1, -1}};

        return LinearMoves.getLinearMoves(directions, myPosition, piece, board);
    }
}
