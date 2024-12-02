package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

    private final String visitorName; // Immutable visitor name
    private final Session session;   // WebSocket session

    /**
     * Constructor to initialize a Connection object.
     *
     * @param visitorName The name of the visitor associated with this connection.
     * @param session     The WebSocket session for this connection.
     */
    public Connection(String visitorName, Session session) {
        if (visitorName == null || visitorName.isEmpty()) {
            throw new IllegalArgumentException("Visitor name cannot be null or empty.");
        }
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }
        this.visitorName = visitorName;
        this.session = session;
    }

    /**
     * Gets the visitor name associated with this connection.
     *
     * @return The visitor name.
     */
    public String getVisitorName() {
        return visitorName;
    }

    /**
     * Gets the WebSocket session for this connection.
     *
     * @return The WebSocket session.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sends a message to the client associated with this connection.
     *
     * @param message The message to send.
     * @throws IOException If an I/O error occurs.
     */
    public void sendMessage(String message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        } else {
            throw new IOException("Cannot send message. The session is closed.");
        }
    }

    @Override
    public String toString() {
        return "Connection{" +
                "visitorName='" + visitorName + '\'' +
                ", session=" + session +
                '}';
    }
}