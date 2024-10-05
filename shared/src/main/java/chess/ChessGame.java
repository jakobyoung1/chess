package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.KING;
import static chess.ChessPiece.PieceType.KNIGHT;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private static ChessBoard board;
    private static TeamColor turn;


    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pc = board.getPiece(startPosition);
        if (pc == null) {
            return null;
        }
        Collection<ChessMove> moves = pc.pieceMoves(board, startPosition);
        ArrayList<ChessMove> afterMoves = new ArrayList<>();
        for (ChessMove m : moves) {
            if (!wouldBeInCheck(pc.getTeamColor(), m, board)) {
                afterMoves.add(m);
            }
        }
        return afterMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pc = board.getPiece(move.getStartPosition());

        if (pc != null && pc.getTeamColor() == turn) {
            Collection<ChessMove> validMoves = pc.pieceMoves(board, move.getStartPosition());

            if (validMoves.contains(move) && !ChessGame.wouldBeInCheck(pc.getTeamColor(), move, board)) {
                if (move.getPromotionPiece()==null) {
                    board.addPiece(move.getEndPosition(), pc);
                    board.addPiece(move.getStartPosition(), null);
                } else {
                    board.addPiece(move.getEndPosition(), new ChessPiece(pc.getTeamColor(), move.getPromotionPiece()));
                    board.addPiece(move.getStartPosition(), null);
                }

                // update king pos
                if (pc.getPieceType() == KING) {
                    if (pc.getTeamColor() == TeamColor.WHITE) {
                        board.updateKingPosition(TeamColor.WHITE);
                    } else {
                        board.updateKingPosition(TeamColor.BLACK);
                    }
                }

                turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

            } else {
                throw new InvalidMoveException("Invalid move.");
            }
        } else {
            throw new InvalidMoveException("No piece at start position.");
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        board.updateKingPosition(teamColor);
        var kingPos = board.getKingPos(teamColor);
        TeamColor opColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        return knightThreatensKing(kingPos, opColor) || kingThreatensKing(kingPos, opColor) || rookOrQueenThreatensKing(kingPos, opColor) || bishopOrQueenThreatensKing(kingPos, opColor) || pawnThreatensKing(kingPos, opColor);
    }

    public static boolean wouldBeInCheck(TeamColor teamColor, ChessMove move, ChessBoard bord) {
        board = bord;
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        ChessPiece targetPiece = board.getPiece(move.getEndPosition());
        board.updateKingPosition(teamColor);
        ChessPosition kingPos = board.getKingPos(teamColor);

        board.addPiece(move.getEndPosition(), movingPiece);
        board.addPiece(move.getStartPosition(), null);

        if (movingPiece.getPieceType() == KING) {
            kingPos = move.getEndPosition();
        }
        boolean inCheck = false;

        if (kingPos != null) {
            inCheck = isInCheckAfterMove(teamColor, kingPos);
        }

        board.addPiece(move.getStartPosition(), movingPiece);
        board.addPiece(move.getEndPosition(), targetPiece);

        return inCheck;
    }

    private static boolean isInCheckAfterMove(TeamColor teamColor, ChessPosition kingPos) {
        TeamColor opColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Check all possible threats to the king's position
        return knightThreatensKing(kingPos, opColor) ||
                kingThreatensKing(kingPos, opColor) ||
                rookOrQueenThreatensKing(kingPos, opColor) ||
                bishopOrQueenThreatensKing(kingPos, opColor) ||
                pawnThreatensKing(kingPos, opColor);
    }

    private static boolean knightThreatensKing(ChessPosition pos, TeamColor opCol) {
        int[][] direction = {
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1},
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1}
        };

        for (int[] move : direction) {
            int nRow = pos.getRow() + move[0];
            int nCol = pos.getColumn() + move[1];
            ChessPosition newPos = new ChessPosition(nRow, nCol);
            if (board.inBounds(nRow, nCol)) {
                ChessPiece piece = board.getPiece(newPos);
                if (piece != null && piece.getTeamColor() == opCol && piece.getPieceType()==KNIGHT) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean kingThreatensKing(ChessPosition pos, TeamColor opCol) {
        int[][] direction = {
                {-1, -1}, {-1, 0}, {-1, 1}, {0, -1},
                {0, 1}, {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] move : direction) {
            int nRow = pos.getRow() + move[0];
            int nCol = pos.getColumn() + move[1];
            ChessPosition newPos = new ChessPosition(nRow, nCol);
            if (board.inBounds(nRow, nCol)) {
                ChessPiece piece = board.getPiece(newPos);
                //System.out.println("Checking for a king in range at: " + nRow + ", " + nCol + "\n");
                if (piece != null && piece.getTeamColor() == opCol && piece.getPieceType()==KING) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean rookOrQueenThreatensKing(ChessPosition pos, TeamColor opCol) {
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        return lineThreatensKing(pos, opCol, directions, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN);
    }

    private static boolean bishopOrQueenThreatensKing(ChessPosition pos, TeamColor opCol) {
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        return lineThreatensKing(pos, opCol, directions, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN);
    }

    private static boolean lineThreatensKing(ChessPosition pos, TeamColor opCol, int[][] directions, ChessPiece.PieceType... pieceTypes) {
        for (int[] direction : directions) {
            int nRow = pos.getRow();
            int nCol = pos.getColumn();

            while (true) {
                nRow += direction[0];
                nCol += direction[1];
                if (!board.inBounds(nRow, nCol)) {
                    break;
                }

                ChessPosition newPos = new ChessPosition(nRow, nCol);
                ChessPiece piece = board.getPiece(newPos);
                if (piece != null) {
                    if (piece.getTeamColor() == opCol && (piece.getPieceType() == pieceTypes[0] || piece.getPieceType() == pieceTypes[1])) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private static boolean pawnThreatensKing(ChessPosition pos, TeamColor opCol) {
        int[][] direction = (opCol == TeamColor.WHITE)
                ? new int[][]{{-1, -1}, {-1, 1}}
                : new int[][]{{1, -1}, {1, 1}};

        for (int[] move : direction) {
            int nRow = pos.getRow() + move[0];
            int nCol = pos.getColumn() + move[1];
            ChessPosition newPos = new ChessPosition(nRow, nCol);
            if (board.inBounds(nRow, nCol)) {
                ChessPiece piece = board.getPiece(newPos);
                if (piece != null && piece.getTeamColor() == opCol && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

    if (!isInCheck(teamColor)) {
        return false;
    }

    for (int row = 1; row <= board.getHeight(); row++) {
        for (int col = 1; col <= board.getWidth(); col++) {
            ChessPosition currentPosition = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(currentPosition);

            if (piece != null && piece.getTeamColor() == teamColor) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, currentPosition);

                for (ChessMove move : validMoves) {
                    if (!ChessGame.wouldBeInCheck(teamColor, move, board)) {
                        return false;
                    }

                }
            }
        }
    }

    // Step 5: If no valid move resolves the check, it is checkmate
    return true;
}


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
    if (isInCheck(teamColor)) {
        return false; // If in check, it cannot be stalemate
    }

    for (int row = 1; row <= board.getHeight(); row++) {
        for (int col = 1; col <= board.getWidth(); col++) {
            ChessPosition currentPosition = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(currentPosition);

            if (piece != null && piece.getTeamColor() == teamColor) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, currentPosition);

                for (ChessMove move : validMoves) {
                    if (!ChessGame.wouldBeInCheck(teamColor, move, board)) {
                        return false; // A valid move exists, so it's not stalemate
                    }
                }
            }
        }
    }

    return true;
}


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        ChessGame.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

}
