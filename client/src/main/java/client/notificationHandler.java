package client;


import websocket.messages.ServerMessage;

import java.io.IOException;

public interface notificationHandler {
    void notify(ServerMessage serverMessage, String message) throws IOException;
}