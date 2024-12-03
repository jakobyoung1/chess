package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import ui.ChessBoardUI;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@ClientEndpoint
public class webSocketFacade {

    public Session session;
    private ChessGame.TeamColor playerColor;
    private final ChessBoardUI chessBoardUI = new ChessBoardUI();
    private final List<GameData> gameDataList = new ArrayList<>();

    public webSocketFacade(String url) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException("Failed to connect to WebSocket server: " + ex.getMessage(), ex);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void handleMessage(String message) {
        System.out.println("Received message: " + message);
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> loadGame(message);
                case NOTIFICATION -> notification(message);
                case ERROR -> error(message);
                default -> System.err.println("Unknown message type.");
            }
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void error(String message) {
        ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
        System.err.println("Error: " + error.getMessage());
    }

    private void notification(String message) {
        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
        System.out.println("Notification: " + notification.getNotificationMessage());
    }

    private void loadGame(String message) {
        LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
        GameData game = (GameData) loadGame.getGame();
        playerColor = determinePlayerColor(game);
        chessBoardUI.displayBoard(game);
    }

    private ChessGame.TeamColor determinePlayerColor(GameData game) {
        // Logic to determine player color based on GameData
        return playerColor; // Adjust as needed
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws IOException {
        playerColor = color;
        JoinPlayerCommand command = new JoinPlayerCommand(authToken, gameID, color);
        sendCommand(command);
    }

    public void joinObserver(String authToken, int gameID) throws IOException {
        JoinObserverCommand command = new JoinObserverCommand(authToken, gameID);
        sendCommand(command);
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        LeaveCommand command = new LeaveCommand(authToken, gameID);
        sendCommand(command);
    }

//    public void redrawBoard(String authToken, int gameID) throws IOException {
//        RedrawBoardCommand command = new RedrawBoardCommand(authToken, gameID);
//        sendCommand(command);
//    }

    public void makeMove(String authToken, ChessMove move, int gameID) throws IOException {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        sendCommand(command);
    }

    public void resign(String authToken, int gameID) throws IOException {
        ResignCommand command = new ResignCommand(authToken, gameID);
        sendCommand(command);
    }

    public void sendCommand(Object command) throws IOException {
        if (session != null && session.isOpen()) {
            String commandJson = new Gson().toJson(command);
            System.out.println("Sending command: " + commandJson);
            session.getBasicRemote().sendText(commandJson);
        } else {
            throw new IllegalStateException("WebSocket is not connected.");
        }
    }
}