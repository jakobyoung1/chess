package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class ServerMessage {
    private final ServerMessageType serverMessageType;

    private final String errorMessage;
    private final String notificationMessage;
    private final ChessGame game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
        this.errorMessage = null;
        this.notificationMessage = null;
    }

    public ServerMessage(ServerMessageType type, String errorMessage) {
        this.serverMessageType = type;
        this.errorMessage = errorMessage;
        this.game = null;
        this.notificationMessage = null;
    }

    public ServerMessage(ServerMessageType type, String errorMessage, String notificationMessage) {
        this.serverMessageType = type;
        this.errorMessage = errorMessage;
        this.notificationMessage = notificationMessage;
        this.game = null;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public ChessGame getGame() {
        return game;
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
        return getServerMessageType() == that.getServerMessageType() &&
                Objects.equals(getErrorMessage(), that.getErrorMessage()) &&
                Objects.equals(getNotificationMessage(), that.getNotificationMessage()) &&
                Objects.equals(getGame(), that.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getErrorMessage(), getNotificationMessage(), getGame());
    }
}
