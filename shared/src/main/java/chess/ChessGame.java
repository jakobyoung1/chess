package chess;

import java.util.ArrayList;
import java.util.Collection;

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
    private static ArrayList<ChessPiece> blackPieces = new ArrayList<>();
    private static ArrayList<ChessPiece> whitePieces = new ArrayList<>();
    private static TeamColor turn;


    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
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
        return pc.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pc = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), pc);
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var kingPos = board.getKingPos(teamColor);
        TeamColor opColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        return knightThreatensKing(kingPos, opColor) || kingThreatensKing(kingPos, opColor) || rookOrQueenThreatensKing(kingPos, opColor) || bishopOrQueenThreatensKing(kingPos, opColor) || pawnThreatensKing(kingPos, opColor);
    }

    public static boolean wouldBeInCheck(TeamColor teamColor, ChessMove move) {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        ChessPiece targetPiece = board.getPiece(move.getEndPosition());
        board.updateKingPosition(teamColor);
        ChessPosition kingPos = board.getKingPos(teamColor);

        board.addPiece(move.getEndPosition(), movingPiece);
        board.addPiece(move.getStartPosition(), null);

        if (movingPiece.getPieceType() == KING) {
            kingPos = move.getEndPosition();
        }

        boolean inCheck = isInCheckAfterMove(teamColor, kingPos);

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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
