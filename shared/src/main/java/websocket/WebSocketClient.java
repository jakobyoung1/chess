package websocket;

import com.google.gson.Gson;
import model.GameData;
import org.glassfish.tyrus.client.ClientManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    private Session session;
    private final Gson gson = new Gson();
    private GameUpdateHandler updateHandler;

    public void connect(String serverUri) throws Exception {
        ClientManager client = ClientManager.createClient();
        client.connectToServer(this, new URI(serverUri));
        System.out.println("Connected to WebSocket server at " + serverUri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened.");
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            if (updateHandler != null) {
                updateHandler.handleUpdate(serverMessage);
            }
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection closed: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void sendCommand(UserGameCommand command) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(gson.toJson(command));
        } else {
            System.err.println("Cannot send command: WebSocket connection is not open.");
        }
    }

    public void setGameUpdateHandler(GameUpdateHandler handler) {
        this.updateHandler = handler;
    }

    public interface GameUpdateHandler {
        void handleUpdate(ServerMessage message);
    }
}
