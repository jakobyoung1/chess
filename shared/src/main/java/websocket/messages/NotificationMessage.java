package websocket.messages;

/**
 * Represents a notification message sent by the server through a WebSocket.
 */
public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be null or empty.");
        }
        this.message = message;
    }

    public String getNotificationMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "serverMessageType=" + getServerMessageType() +
                ", notificationMessage='" + message + '\'' +
                '}';
    }
}