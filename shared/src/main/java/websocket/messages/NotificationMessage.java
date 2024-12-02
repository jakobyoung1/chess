package websocket.messages;

/**
 * Represents a notification message sent by the server through a WebSocket.
 */
public class NotificationMessage extends ServerMessage {

    private final String notificationMessage;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be null or empty.");
        }
        this.notificationMessage = message;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "serverMessageType=" + getServerMessageType() +
                ", notificationMessage='" + notificationMessage + '\'' +
                '}';
    }
}