package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class ChessWebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ChessWebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT:
                    handleConnect(session, message);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(session, message);
                    break;
                case LEAVE:
                    handleLeave(session, message);
                    break;
                case RESIGN:
                    handleResign(session, message);
                    break;
                default:
                    sendError(session, "Unknown command type.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Invalid message format.");
        }
    }

    private void handleConnect(Session session, String action) {
        JoinPlayerCommand command = new Gson().fromJson(action, JoinPlayerCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        try {
            GameData game = gameDAO.getGame(gameID);
            AuthData auth = authDAO.getAuth(authToken);
            String username = auth.username();

            if (game == null) {
                sendError(session, "Game not found.");
                return;
            }

            connections.add(gameID, authToken, session);

            String message = username + " has joined the game.";
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(authToken, notification, gameID);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            connections.sendMessage(gameID, authToken, new Gson().toJson(loadGameMessage));
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Failed to connect to the game.");
        }
    }

    private void handleMakeMove(Session session, String action) {
        MakeMoveCommand command = new Gson().fromJson(action, MakeMoveCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessMove move = command.getMove();

        try {
            GameData game = gameDAO.getGame(gameID);
            AuthData auth = authDAO.getAuth(authToken);
            String username = auth.username();

            if (game == null || game.isGameOver()) {
                sendError(session, "Game is either over or not found.");
                return;
            }

            if (!username.equals(game.getWhiteUsername()) && !username.equals(game.getBlackUsername())) {
                sendError(session, "You are not a participant in this game.");
                return;
            }

            game.getGame().makeMove(move);
            gameDAO.updateGame(gameID, game);

            NotificationMessage notification = new NotificationMessage(username + " made a move: " + move);
            connections.broadcast(authToken, notification, gameID);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            connections.broadcast("", loadGameMessage, gameID);
        } catch (InvalidMoveException e) {
            sendError(session, "Invalid move.");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error processing move.");
        }
    }

    private void handleLeave(Session session, String action) {
        LeaveGameCommand command = new Gson().fromJson(action, LeaveGameCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        try {
            connections.remove(gameID, authToken, session);

            String message = "A player has left the game.";
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast("", notification, gameID);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error leaving the game.");
        }
    }

    private void handleResign(Session session, String action) {
        ResignCommand command = new Gson().fromJson(action, ResignCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        try {
            GameData game = gameDAO.getGame(gameID);
            AuthData auth = authDAO.getAuth(authToken);
            String username = auth.username();

            if (game == null || game.isGameOver()) {
                sendError(session, "Game is either over or not found.");
                return;
            }

            game.setGameOver(true);
            gameDAO.updateGame(gameID, game);

            String message = username + " has resigned. Game over.";
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast("", notification, gameID);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error processing resignation.");
        }
    }

    private void sendError(Session session, String message) {
        try {
            ErrorMessage errorMessage = new ErrorMessage(message);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}