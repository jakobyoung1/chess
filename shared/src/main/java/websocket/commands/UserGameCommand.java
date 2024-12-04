package websocket.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class UserGameCommand {

    CommandType commandType; // Represents the type of command
    private final String authToken;        // Authentication token for the command

    // Constructor to initialize the command type and authentication token
    protected UserGameCommand(CommandType commandType, String authToken) {
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
        REDRAW,
        JOIN_OBSERVER
    }

    public static UserGameCommand fromJson(String json) {
        // Parse the commandType field first
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        CommandType commandType = CommandType.valueOf(jsonObject.get("commandType").getAsString());

        // Use the commandType to decide the subclass
        switch (commandType) {
            case MAKE_MOVE:
                return new Gson().fromJson(json, MakeMoveCommand.class);
            case LEAVE:
                return new Gson().fromJson(json, LeaveCommand.class);
            case RESIGN:
                return new Gson().fromJson(json, ResignCommand.class);
            case JOIN_PLAYER:
                return new Gson().fromJson(json, JoinPlayerCommand.class);
            case REDRAW:
                return new Gson().fromJson(json, RedrawBoardCommand.class);
            case JOIN_OBSERVER:
                return new Gson().fromJson(json, JoinObserverCommand.class);
            default:
                throw new IllegalArgumentException("Unsupported command type: " + commandType);
        }
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