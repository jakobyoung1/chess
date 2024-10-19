package server;

import chess.ChessMove;

public record MoveRequest(String gameId, ChessMove move) {}
