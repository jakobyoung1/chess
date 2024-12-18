package websocket.commands;

public class LeaveCommand extends UserGameCommand {

    private final Integer gameID; // Unique identifier for the game

    public LeaveCommand(String authToken, Integer gameID) {
        // Call the parent constructor with the command type and auth token
        super(CommandType.LEAVE, authToken);
        if (gameID == null) {
            throw new IllegalArgumentException("GameID cannot be null.");
        }
        this.gameID = gameID;
    }

    // Getter for game ID
    public Integer getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return "LeaveCommand{" +
                "gameID=" + gameID +
                ", authToken='" + getAuthToken() + '\'' +
                ", commandType=" + getCommandType() +
                '}';
    }
}