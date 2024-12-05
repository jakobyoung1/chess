package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import model.GameData;
import ui.ChessBoardUI;
import ui.GamePlayUI;
import ui.PostLoginUI;
import websocket.commands.ConnectCommand;
import websocket.commands.JoinObserverCommand;
import websocket.commands.JoinPlayerCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

@ClientEndpoint
public class webSocketFacade {

    public Session session;
    notificationHandler notificationHandler;
    private boolean messageHandlerSet = true;
    private ChessGame.TeamColor playerColor;
    private ChessBoardUI draw = new ChessBoardUI();

    public webSocketFacade(String url) throws Exception {
        try {
            url = url.replace("http://", "ws://").replace("https://", "wss://");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.notificationHandler = new notificationHandler() {
                @Override
                public void notify(ServerMessage serverMessage, String message) throws IOException {
                    //System.out.println("SERVER MESSAGE: " + serverMessage);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> notification(message);
                        case ERROR -> error(message);
                        case LOAD_GAME -> loadGame(message);
                        case null -> System.out.println("NULL message type");
                    }
                }
            };

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    try {
                        notificationHandler.notify(notification, message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to connect to WebSocket server: " + ex.getMessage(), ex);
        }
    }

    public void error(String message) {
        ServerMessage error  = ServerMessage.fromJson(message);
    }

    public void notification(String message) {
        NotificationMessage notification  = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(notification.getNotificationMessage());
    }

    public void loadGame(String message) throws IOException {
        LoadGameMessage loadGame  = new Gson().fromJson(message, LoadGameMessage.class);
        System.out.println("LOADGAME MESSAGE: " + message);
        GameData game = loadGame.getGameData();
        String bUsername = game.getBlackUsername();
        String wUsername = game.getWhiteUsername();
    }

    /**
     * Joins a game as a player.
     */
    public void joinGame(String authToken, int gameId, ChessGame.TeamColor color) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket is not connected.");
        }
        playerColor = color;
        ConnectCommand command = new ConnectCommand(authToken, gameId, color);
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

    public void sendMessage(String message) throws IOException {
        if (session != null && session.isOpen()) {
            System.out.println("Sending WS message: " + message);
            session.getBasicRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open.");
        }
    }


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        //System.out.println("WebSocket connection closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
        throwable.printStackTrace();
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

    public void close() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                //System.out.println("WebSocket connection closed.");
            } catch (IOException e) {
                System.err.println("Error closing WebSocket connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                session = null; // Ensure session is null after closing
            }
        } else {
            System.out.println("WebSocket connection is already closed.");
        }
    }
}