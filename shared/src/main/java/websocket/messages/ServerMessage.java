package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

import java.util.Objects;

/**
 * Represents a general server message that can be sent through a WebSocket.
 */
public class ServerMessage {

    private final ServerMessageType serverMessageType;
    private GameData game;

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

    public GameData getGame() {
        return (GameData) game;
    }

    public void setGame(GameData game) {
        this.game = game;
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
                ", game=" + game +
                '}';
    }
}