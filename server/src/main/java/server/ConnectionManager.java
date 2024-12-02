package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // Thread-safe map for game ID to sessions
    private final Map<Integer, Map<String, Session>> connections = new ConcurrentHashMap<>();

    /**
     * Adds a session for a specific game and user.
     *
     * @param gameID    The ID of the game.
     * @param authToken The user's auth token.
     * @param session   The WebSocket session.
     */
    public void add(int gameID, String authToken, Session session) {
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>())
                .put(authToken, session);
    }

    /**
     * Removes a session for a specific game and user.
     *
     * @param gameID    The ID of the game.
     * @param authToken The user's auth token.
     */
    public void remove(int gameID, String authToken) {
        Map<String, Session> sessionHashMap = connections.get(gameID);
        if (sessionHashMap != null) {
            sessionHashMap.remove(authToken);
            // Optionally remove the entire gameID entry if no users are left
            if (sessionHashMap.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    /**
     * Sends a message to a specific user in a game.
     *
     * @param gameID    The ID of the game.
     * @param authToken The user's auth token.
     * @param message   The message to send.
     */
    public void sendMessage(int gameID, String authToken, String message) {
        Map<String, Session> sessionMap = connections.get(gameID);
        if (sessionMap != null) {
            Session session = sessionMap.get(authToken);
            if (session != null && session.isOpen()) {
                try {
                    session.getRemote().sendString(message);
                } catch (IOException e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Broadcasts a message to all users in a game, excluding one.
     *
     * @param excludeAuthToken The auth token to exclude from the broadcast.
     * @param message          The message to broadcast.
     * @param gameID           The ID of the game.
     */
    public void broadcast(String excludeAuthToken, ServerMessage message, int gameID) {
        Map<String, Session> sessionMap = connections.get(gameID);
        if (sessionMap != null) {
            List<String> closedConnections = new ArrayList<>();
            for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
                String userAuth = entry.getKey();
                Session session = entry.getValue();

                if (!session.isOpen()) {
                    closedConnections.add(userAuth); // Mark closed sessions for removal
                } else if (!Objects.equals(userAuth, excludeAuthToken)) {
                    try {
                        session.getRemote().sendString(new Gson().toJson(message));
                    } catch (IOException e) {
                        System.err.println("Error broadcasting message: " + e.getMessage());
                    }
                }
            }

            // Clean up closed connections
            for (String closed : closedConnections) {
                sessionMap.remove(closed);
            }
        }
    }
}