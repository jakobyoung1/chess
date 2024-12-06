package websocket.commands;

import chess.ChessPosition;

import static websocket.commands.UserGameCommand.CommandType.HIGHLIGHT_MOVES;

public class HighlightLegalMovesCommand extends UserGameCommand{
    private ChessPosition pos;
    public HighlightLegalMovesCommand(String authToken, ChessPosition pos) {
        super(HIGHLIGHT_MOVES, authToken);
        this.pos = pos;
    }

    public ChessPosition getPos() {
        return pos;
    }

}