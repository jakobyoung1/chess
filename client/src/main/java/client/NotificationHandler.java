package client;

import websocket.messages.*;

import java.io.IOException;

public interface NotificationHandler {
    void notify(ServerMessage serverMessage, String message) throws IOException;
}