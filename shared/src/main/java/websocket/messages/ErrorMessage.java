package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

/**
 * Represents an error message sent from the server through WebSocket.
 */
public class ErrorMessage  extends ServerMessage {

    private final String errorMessage;

    /**
     * Constructs an ErrorMessage with the specified message.
     *
     * @param message A detailed message describing the error.
     */
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    /**
     * Retrieves the error message.
     *
     * @return The error message.
     */
    public String getMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "message='" + errorMessage + '\'' +
                '}';
    }
}