package websocket.commands;

import chess.ChessMove;

/**
 * Represents a WebSocket command sent by a client to make a move in the game.
 */
public class MakeMoveCommand extends UserGameCommand {

    private final int gameID;  // Add gameID to match requirements
    private final ChessMove move;

    /**
     * Constructs a MakeMoveCommand with the specified parameters.
     *
     * @param authToken The authentication token of the user.
     * @param gameID    The ID of the game where the move is being made.
     * @param move      The move being made in the game.
     */
    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken);
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null.");
        }
        this.gameID = gameID;
        this.move = move;
    }

    /**
     * Retrieves the game ID associated with this command.
     *
     * @return The game ID.
     */
    public int getGameID() {
        return gameID;
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
                ", gameID=" + gameID +
                ", move=" + move +
                '}';
    }
}