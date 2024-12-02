package websocket;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

@ClientEndpoint
public class WebSocketClient {

    private final Gson gson = new Gson();
    private URI serverUri;
    private Session session;
    private Consumer<ServerMessage> gameUpdateHandler;

    public WebSocketClient() {
    }

    public void connect(String uri) throws Exception {
        try {
            if (session != null && session.isOpen()) {
                System.out.println("Already connected to WebSocket server.");
                return;
            }
            this.serverUri = new URI(uri);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, serverUri);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + uri);
            throw e;
        } catch (Exception e) {
            System.err.println("Failed to connect to WebSocket server: " + e.getMessage());
            throw e;
        }
    }

    public void disconnect() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("WebSocket session closed.");
            }
        } catch (Exception e) {
            System.err.println("Error closing WebSocket session: " + e.getMessage());
        } finally {
            session = null;
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket server. Session ID: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            if (gameUpdateHandler != null) {
                gameUpdateHandler.accept(serverMessage);
            } else {
                System.out.println("Received a message but no handler is set: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to process WebSocket message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from WebSocket server. Reason: " + closeReason.getReasonPhrase());
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error occurred: " + throwable.getMessage());
        if (session != null) {
            System.err.println("Session ID: " + session.getId());
        }
        throwable.printStackTrace();
    }

    public void setGameUpdateHandler(Consumer<ServerMessage> handler) {
        this.gameUpdateHandler = handler;
    }

    public void sendCommand(UserGameCommand command) {
        if (session != null && session.isOpen()) {
            try {
                String jsonCommand = gson.toJson(command);
                session.getAsyncRemote().sendText(jsonCommand);
                System.out.println("Sent command: " + jsonCommand);
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket command: " + e.getMessage());
            }
        } else {
            System.err.println("Cannot send command: WebSocket session is not open.");
        }
    }
}
