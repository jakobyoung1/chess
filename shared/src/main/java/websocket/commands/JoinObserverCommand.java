package websocket.commands;

public class JoinObserverCommand extends UserGameCommand {

    private final Integer gameID;

    public JoinObserverCommand(String authToken, Integer gameID) {
        super(CommandType.JOIN_OBSERVER, authToken);
        if (gameID == null) {
            throw new IllegalArgumentException("GameID cannot be null.");
        }
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public Integer getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return "JoinObserverCommand{" +
                "gameID=" + gameID +
                ", authToken='" + getAuthToken() + '\'' +
                ", commandType=" + getCommandType() +
                '}';
    }
}