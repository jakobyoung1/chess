package client;


import websocket.messages.ServerMessage;

public interface notificationHandler {
    void notify(ServerMessage serverMessage, String message);
}