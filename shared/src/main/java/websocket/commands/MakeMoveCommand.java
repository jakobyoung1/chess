package websocket.commands;

import chess.ChessMove;

/**
 * Represents a WebSocket command sent by a client to make a move in the game.
 */
public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;

    /**
     * Constructs a MakeMoveCommand with the specified parameters.
     *
     * @param authToken The authentication token of the user.
     * @param move      The move being made in the game.
     */
    public MakeMoveCommand(String authToken, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken);
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null.");
        }
        this.move = move;
    }

    /**
     * Retrieves the move associated with this command.
     *
     * @return The move being made.
     */
    public ChessMove getMove() {
        return move;
    }

    @Override
    public String toString() {
        return "MakeMoveCommand{" +
                "commandType=" + getCommandType() +
                ", authToken='" + getAuthToken() + '\'' +
                ", move=" + move +
                '}';
    }
}