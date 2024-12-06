package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
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
public class WebSocketFacade {

    public Session session;
    NotificationHandler notificationHandler;
    private boolean messageHandlerSet = true;
    private ChessGame.TeamColor playerColor;
    private ChessBoardUI draw = new ChessBoardUI();
    private GamePlayUI gamePlay;

    public WebSocketFacade(String url) throws Exception {
        try {
            url = url.replace("http://", "ws://").replace("https://", "wss://");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.notificationHandler = new NotificationHandler() {
                @Override
                public void notify(ServerMessage serverMessage, String message) throws Exception {
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> notification(message);
                        case ERROR -> error(message);
                        case LOAD_GAME -> loadGame(message);
                    }
                }
            };

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    try {
                        notificationHandler.notify(notification, message);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to connect to WebSocket server: " + ex.getMessage(), ex);
        }
    }

    public void error(String message) {
        ErrorMessage error  = (ErrorMessage) ErrorMessage.fromJson(message);
        System.out.println(error.getMessage());
    }

    public void notification(String message) {
        NotificationMessage notification  = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(notification.getNotificationMessage());
    }

    public void loadGame(String message) throws Exception {
        LoadGameMessage loadGame  = new Gson().fromJson(message, LoadGameMessage.class);
        System.out.println("LOADGAME MESSAGE: " + message);
        GameData game = loadGame.getGameData();
        String bUsername = game.getBlackUsername();
        String wUsername = game.getWhiteUsername();
        gamePlay.drawFromLoad(game);
        System.out.print("Enter command (Make Move, Resign, Leave, Highlight Legal Moves, Redraw Board, Help): \n");

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

    public void sendMessage(String message) throws IOException {
        if (session != null && session.isOpen()) {
            //System.out.println("Sending WS message: " + message);
            session.getBasicRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket session is not open.");
        }
    }

    public void sendCommand(Object command) throws IOException {
        if (session != null && session.isOpen()) {
            String commandJson = new Gson().toJson(command);
            //System.out.println("Sending command: " + commandJson);
            session.getBasicRemote().sendText(commandJson);
        } else {
            throw new IllegalStateException("WebSocket session is not open.");
        }
    }

    public void setGamePlayUI(GamePlayUI gamePlayUI) {
        gamePlay = gamePlayUI;
    }
}