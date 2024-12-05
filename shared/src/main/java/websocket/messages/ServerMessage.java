package websocket.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Objects;

/**
 * Represents a general server message that can be sent through a WebSocket.
 */
public class ServerMessage {

    private final ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
                "serverMessageType=" + serverMessageType +
                '}';
    }

    /**
     * Deserialize JSON into a specific subclass of ServerMessage based on serverMessageType.
     *
     * @param json The JSON string to parse.
     * @return A subclass of ServerMessage.
     */
    public static ServerMessage fromJson(String json) {
        // Parse the serverMessageType field first
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        ServerMessageType serverMessageType = ServerMessageType.valueOf(
                jsonObject.get("serverMessageType").getAsString()
        );

        // Use the serverMessageType to decide the subclass
        switch (serverMessageType) {
            case LOAD_GAME:
                return new Gson().fromJson(json, LoadGameMessage.class);
            case ERROR:
                return new Gson().fromJson(json, ErrorMessage.class);
            case NOTIFICATION:
                return new Gson().fromJson(json, NotificationMessage.class);
            default:
                throw new IllegalArgumentException("Unsupported server message type: " + serverMessageType);
        }
    }
}