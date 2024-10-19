package server;

import chess.ChessGame;

public record StartGameResult(int gameId, ChessGame game, String message) {}
