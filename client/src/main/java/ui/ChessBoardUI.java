package ui;

import chess.*;
import client.ServerFacade;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    public static final String SET_BG_COLOR_CREME_YELLOW = "\u001B[48;5;228m";
    public static final String SET_BG_COLOR_CREME_BROWN = "\u001B[48;5;94m";
    private Collection<ChessMove> validMoves = null;
    HashSet<ChessPosition> highlightSquares = null;
    ChessPosition pos;
    public ChessBoardUI(){

    }

    public void displayGame(GameData gameData, ChessGame.TeamColor playerColor, ChessPosition pos, ServerFacade server) throws Exception {
        if (pos == null) {
            // Clear highlights when no position is specified
            this.pos = null;
            validMoves = null;
            highlightSquares = null;
        } else {
            this.pos = pos;
            validMoves = gameData.getGame().validMoves(pos);
            highlightSquares = getValidPositions();
        }

        var gameID = gameData.getGameId();
        var games = server.listGames();
        gameData = games.get(gameID-1);
        if (pos != null) {
            this.pos = pos;
            validMoves = gameData.getGame().validMoves(pos);
            highlightSquares = getValidPositions();
        }
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var output = "\n" + gameData.getGameName() + ":\n";
        String wUsername = gameData.getWhiteUsername();
        String bUsername = gameData.getBlackUsername();
        if (wUsername == null){
            wUsername = "No player added";
        }
        if (bUsername == null){
            bUsername = "No player added";
        }
        output += "White Player: " + wUsername + "\n";
        output += "Black Player: " + bUsername + "\n";
        if (gameData.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE){
            output += wUsername + "'s turn to move";
        }
        else {
            output += bUsername + "'s turn to move";
        }
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(output);
        out.print("\n");
        if (playerColor == ChessGame.TeamColor.BLACK){
            drawBoardBlack(out, gameData.getGame());
        }
        else {
            drawBoardWhite(out, gameData.getGame());
        }

    }



    private void drawBoardBlack(PrintStream out, ChessGame game) {
        writeLetters(out);

        for (int row = 1; row < 9; row++) {
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(row);

            for (int col = 1; col < 9; col++) {
                ChessPosition current = new ChessPosition(row, 9 - col);
                printSquare(out, game, row, 9 - col, current);
            }

            out.print(RESET_BG_COLOR);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(row);
            out.print("\n");
        }

        writeLetters(out);
    }

    private static void writeLetters(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("  H ");
        out.print(" G ");
        out.print(" F ");
        out.print(" E ");
        out.print(" D ");
        out.print(" C ");
        out.print(" B ");
        out.print(" A \n");
    }

    private void doMainPieceWhite(PrintStream out, ChessGame game, int j, int i) {
        setBlue(out);
        evalBoard(out, game, j, i);
    }

    private void doHighlightWhite(PrintStream out, ChessGame game, int j, int i) {
        if ((j % 2) != 0){
            if ((i % 2) == 0){
                setGreen(out);
            }
            else {
                setDarkGreen(out);
            }
            evalBoard(out, game, j, i);
        }
        else {
            if ((i % 2) == 0){
                setDarkGreen(out);
            }
            else {
                setGreen(out);
            }
            evalBoard(out, game, j, i);
        }
    }

    private void drawBoardWhite(PrintStream out, ChessGame game) {
        drawColumnHeaders(out);

        for (int row = 1; row < 9; row++) {
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(9 - row);

            for (int col = 1; col < 9; col++) {
                ChessPosition current = new ChessPosition(9 - row, col);
                printSquare(out, game, 9 - row, col, current);
            }

            out.print(RESET_BG_COLOR);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(9 - row);
            out.print("\n");
        }

        drawColumnHeaders(out);
    }

    private void drawColumnHeaders(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("  A ");
        out.print(" B ");
        out.print(" C ");
        out.print(" D ");
        out.print(" E ");
        out.print(" F ");
        out.print(" G ");
        out.print(" H \n");
    }

    private void printSquare(PrintStream out, ChessGame game, int row, int col, ChessPosition current) {
        if (current.equals(pos)) {
            doMainPieceWhite(out, game, row, col);
        } else if (highlightSquares != null && highlightSquares.contains(current)) {
            doHighlightWhite(out, game, row, col);
        } else {
            printDefaultSquare(out, game, row, col);
        }
    }

    private void printDefaultSquare(PrintStream out, ChessGame game, int row, int col) {
        boolean isOddRow = (row % 2) != 0;
        boolean isEvenCol = (col % 2) == 0;

        if (isOddRow) {
            if (isEvenCol) {
                setYellow(out);
            } else {
                setBrown(out);
            }
        } else {
            if (isEvenCol) {
                setBrown(out);
            } else {
                setYellow(out);
            }
        }

        evalBoard(out, game, row, col);
    }



    public static void evalBoard(PrintStream out, ChessGame game, int row, int col){
        ChessBoard board = game.getBoard();
        ChessPosition pos = new ChessPosition(row, col);
        if (board.getPiece(pos) != null){
            ChessPiece piece = board.getPiece(pos);
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                out.print(SET_TEXT_COLOR_WHITE);
            }
            else {
                out.print(SET_TEXT_COLOR_BLACK);
            }
            out.print(" " + getPieceType(piece) + " ");
        }
        else {
            out.print("   ");
        }
    }


    private HashSet<ChessPosition> getValidPositions() {
        HashSet<ChessPosition> highlightMoves = new HashSet<>();
        for (ChessMove move: validMoves){
            highlightMoves.add(move.getEndPosition());
        }
        return highlightMoves;
    }



    public static String getPieceType(ChessPiece piece){
        return switch (piece.getPieceType()) {
            case QUEEN -> "Q";
            case KING -> "K";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
    }

    private static void setBrown(PrintStream out) {
        out.print(SET_BG_COLOR_CREME_BROWN);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_CREME_YELLOW);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_WHITE);
    }




}