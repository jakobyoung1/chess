package websocket;

import com.google.gson.Gson;
import org.glassfish.tyrus.client.ClientManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

/**
 * Handles WebSocket communication for the chess client using Tyrus.
 */
@ClientEndpoint
public class WebSocketClient {

    private Session session;
    private final Gson gson = new Gson();
    private MessageHandler messageHandler;

    /**
     * Connects to the WebSocket server.
     *
     * @param serverUri The URI of the WebSocket server.
     * @throws Exception If the connection fails.
     */
    public void connect(String serverUri) throws Exception {
        ClientManager client = ClientManager.createClient();
        client.connectToServer(this, new URI(serverUri));
        System.out.println("Connected to WebSocket server at " + serverUri);
    }

    /**
     * Handles the opening of a WebSocket connection.
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened.");
    }

    /**
     * Handles incoming messages from the WebSocket server.
     *
     * @param message The incoming message as a JSON string.
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

            if (messageHandler != null) {
                messageHandler.handle(serverMessage);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    /**
     * Handles the closing of a WebSocket connection.
     *
     * @param session The WebSocket session.
     * @param reason  The reason for closing the connection.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection closed: " + reason);
    }

    /**
     * Handles errors during WebSocket communication.
     *
     * @param session The WebSocket session.
     * @param throwable The error that occurred.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    /**
     * Sends a message to the WebSocket server.
     *
     * @param userGameCommand The command to send, serialized to JSON.
     */
    public void sendMessage(UserGameCommand userGameCommand) {
        if (session != null && session.isOpen()) {
            String jsonMessage = gson.toJson(userGameCommand);
            session.getAsyncRemote().sendText(jsonMessage);
        } else {
            System.err.println("Cannot send message: WebSocket connection is not open.");
        }
    }

    /**
     * Sets a handler for processing incoming messages from the server.
     *
     * @param handler The message handler.
     */
    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    /**
     * Interface for handling server messages.
     */
    public interface MessageHandler {
        void handle(ServerMessage message);
    }
}
