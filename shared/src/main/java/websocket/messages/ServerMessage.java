package websocket.messages;

import model.GameData;

import java.util.Objects;

/**
 * Represents a general server message that can be sent through a WebSocket.
 */
public class ServerMessage {

    private final ServerMessageType serverMessageType;
    private Object game; // For game state updates

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        if (type == ServerMessageType.NOTIFICATION) {
            throw new IllegalArgumentException("Use NotificationMessage for notifications.");
        }
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public GameData getGame() {
        return (GameData) game;
    }

    public void setGame(Object game) {
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