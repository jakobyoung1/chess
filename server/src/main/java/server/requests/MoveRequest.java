package server.requests;

import chess.ChessMove;

public record MoveRequest(Integer gameId, ChessMove move) {}
