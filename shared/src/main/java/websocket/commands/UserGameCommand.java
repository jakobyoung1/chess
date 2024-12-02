package websocket.commands;

public abstract class UserGameCommand {

    private final CommandType commandType; // Represents the type of command
    private final String authToken;        // Authentication token for the command

    // Constructor to initialize the command type and authentication token
    protected UserGameCommand(CommandType commandType, String authToken) {
        if (commandType == null) {
            throw new IllegalArgumentException("CommandType cannot be null.");
        }
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("AuthToken cannot be null or empty.");
        }
        this.commandType = commandType;
        this.authToken = authToken;
    }

    // Enum representing different types of commands
    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN,
        JOIN_PLAYER,
        JOIN_OBSERVER
    }

    // Getter for the command type
    public CommandType getCommandType() {
        return commandType;
    }

    // Getter for the authentication token
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String toString() {
        return "UserGameCommand{" +
                "commandType=" + commandType +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}