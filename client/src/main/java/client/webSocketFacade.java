package client;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.JoinObserverCommand;
import websocket.commands.JoinPlayerCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class webSocketFacade {

    public Session session;
    private boolean messageHandlerSet = false;

    public webSocketFacade(String url) throws Exception {
        try {
            // Ensure the URL uses the WebSocket scheme
            url = url.replace("http://", "ws://").replace("https://", "wss://");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to connect to WebSocket server: " + ex.getMessage(), ex);
        }
    }

    /**
     * Checks if the WebSocket session is open.
     */
    public boolean isOpen() {
        return session != null && session.isOpen();
    }

    /**
     * Connects to the WebSocket server if not already connected.
     */
    public void connect() throws IOException, DeploymentException, URISyntaxException {
        if (session == null || !session.isOpen()) {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, new URI("ws://localhost:8080/ws"));
        }
    }

    /**
     * Adds a message handler for processing incoming WebSocket messages.
     */
    public void addMessageHandler(MessageHandler.Whole<String> handler) {
        if (session != null) {
            session.addMessageHandler(handler);
        } else {
            throw new IllegalStateException("WebSocket session is not initialized.");
        }
    }

    /**
     * Joins a game as a player.
     */
    public void joinGame(String authToken, int gameId, ChessGame.TeamColor color) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket is not connected.");
        }

        JoinPlayerCommand command = new JoinPlayerCommand(authToken, gameId, color);
        sendMessage(new Gson().toJson(command));
    }

    /**
     * Joins a game as an observer.
     */
    public void joinObserver(String authToken, int gameId) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket is not connected.");
        }

        JoinObserverCommand command = new JoinObserverCommand(authToken, gameId);
        sendMessage(new Gson().toJson(command));
    }

    /**
     * Sends a message through the WebSocket connection.
     */
    public void sendMessage(String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open.");
        }
    }

    /**
     * Closes the WebSocket connection.
     */
    public void disconnect() {
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received WebSocket message: " + message);
    }

    public void sendCommand(Object command) throws IOException {
        if (session != null && session.isOpen()) {
            String commandJson = new Gson().toJson(command);
            System.out.println("Sending command: " + commandJson);
            session.getBasicRemote().sendText(commandJson);
        } else {
            throw new IllegalStateException("WebSocket session is not open.");
        }
    }

    private void handleMessage(String rawMessage) {
        System.out.println("Received message: " + rawMessage);
        try {
            ServerMessage serverMessage = new Gson().fromJson(rawMessage, ServerMessage.class);

            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage loadGameMessage = new Gson().fromJson(rawMessage, LoadGameMessage.class);
                    // Process and display the game state
                    System.out.println("Game state updated.");
                }
                case NOTIFICATION -> {
                    NotificationMessage notificationMessage = new Gson().fromJson(rawMessage, NotificationMessage.class);
                    // Display notifications
                    System.out.println("Notification: " + notificationMessage.getNotificationMessage());
                }
                case ERROR -> {
                    ErrorMessage errorMessage = new Gson().fromJson(rawMessage, ErrorMessage.class);
                    // Display errors
                    System.err.println("Error: " + errorMessage.getMessage());
                }
                default -> {
                    // Handle unknown message types
                    System.err.println("Unknown message type received: " + serverMessage.getServerMessageType());
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + rawMessage);
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    public boolean isMessageHandlerSet() {
        return messageHandlerSet;
    }

    public void setMessageHandler(MessageHandler.Whole<String> handler) {
        if (session != null && session.isOpen()) {
            session.addMessageHandler(handler);
            messageHandlerSet = true;
        } else {
            throw new IllegalStateException("WebSocket session is not open.");
        }
    }
}