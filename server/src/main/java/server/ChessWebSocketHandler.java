package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ChessWebSocketHandler {

    private static GameDAO gameDAO;
    private static AuthDAO authDAO;
    private static UserDAO userDAO;

    private static final Map<Session, String> activeSessions = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    public static void initialize(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        ChessWebSocketHandler.gameDAO = gameDAO;
        ChessWebSocketHandler.authDAO = authDAO;
        ChessWebSocketHandler.userDAO = userDAO;
        System.out.println("ChessWebSocketHandler initialized with GameDAO, AuthDAO, and UserDAO.");
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connection established: " + session);
        activeSessions.put(session, null);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session + " Code: " + statusCode + " Reason: " + reason);
        activeSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received message: " + message);

        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT:
                    handleConnect(session, command);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(session, command);
                    break;
                case LEAVE:
                    handleLeave(session, command);
                    break;
                case RESIGN:
                    handleResign(session, command);
                    break;
                default:
                    sendError(session, "Unknown command type.");
            }
        } catch (Exception e) {
            sendError(session, "Invalid message format.");
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        error.printStackTrace();
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            String username = authDAO.getUsername(command.getAuthToken());
            if (username == null) {
                sendError(session, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found.");
                return;
            }

            activeSessions.put(session, username);

            sendLoadGame(session, gameData);
            sendNotificationToAllExcept(session, username + " connected to game " + gameData.getGameName());

        } catch (Exception e) {
            sendError(session, "Error processing connect command: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
        try {
            String username = authDAO.getUsername(command.getAuthToken());
            if (username == null) {
                sendError(session, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found.");
                return;
            }

            gameData.makeMove(command.getMove());
            gameDAO.updateGame(command.getGameID(), gameData);

            broadcastGameUpdate(gameData);
            sendNotificationToAllExcept(session, username + " made a move.");

        } catch (Exception e) {
            sendError(session, "Error processing make move command: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        String username = activeSessions.remove(session);
        if (username != null) {
            sendNotificationToAllExcept(session, username + " left the game.");
        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        try {
            String username = authDAO.getUsername(command.getAuthToken());
            if (username == null) {
                sendError(session, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found.");
                return;
            }

            gameData.setGameOver(true);
            gameDAO.updateGame(command.getGameID(), gameData);

            sendNotificationToAllExcept(session, username + " resigned from the game.");
        } catch (Exception e) {
            sendError(session, "Error processing resign command: " + e.getMessage());
        }
    }

    private void sendLoadGame(Session session, GameData gameData) {
        try {
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(gameData);
            session.getRemote().sendString(gson.toJson(loadGameMessage));
        } catch (IOException e) {
            System.out.println("Error sending game data: " + e.getMessage());
        }
    }

    private void broadcastGameUpdate(GameData gameData) {
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGameMessage.setGame(gameData);

        String message = gson.toJson(loadGameMessage);
        activeSessions.keySet().forEach(session -> {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                System.out.println("Error broadcasting game update: " + e.getMessage());
            }
        });
    }

    private void sendNotificationToAllExcept(Session exceptSession, String notification) {
        ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notificationMessage.setNotificationMessage(notification);

        String message = gson.toJson(notificationMessage);
        activeSessions.keySet().stream()
                .filter(session -> session != exceptSession)
                .forEach(session -> {
                    try {
                        session.getRemote().sendString(message);
                    } catch (IOException e) {
                        System.out.println("Error sending notification: " + e.getMessage());
                    }
                });
    }

    private void sendError(Session session, String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setNotificationMessage(errorMessage);
            session.getRemote().sendString(gson.toJson(error));
        } catch (IOException e) {
            System.out.println("Error sending error message: " + e.getMessage());
        }
    }
}
