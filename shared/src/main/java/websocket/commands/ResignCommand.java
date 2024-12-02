package websocket.commands;

/**
 * Represents a WebSocket command sent by a client to resign from the game.
 */
public class ResignCommand extends UserGameCommand {

    private final int gameID; // The ID of the game the user is resigning from

    /**
     * Constructs a ResignCommand with the specified parameters.
     *
     * @param authToken The authentication token of the user.
     * @param gameID    The ID of the game the user is resigning from.
     */
    public ResignCommand(String authToken, int gameID) {
        super(CommandType.RESIGN, authToken);
        this.gameID = gameID;
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
     * Retrieves the authentication token associated with this command.
     *
     * @return The authentication token.
     */
    @Override
    public String getAuthToken() {
        return super.getAuthToken();
    }

    @Override
    public String toString() {
        return "ResignCommand{" +
                "commandType=" + getCommandType() +
                ", authToken='" + getAuthToken() + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}