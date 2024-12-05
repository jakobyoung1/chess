package websocket.messages;

import model.GameData;

/**
 * Represents a message to load a game's state sent by the server through a WebSocket.
 */
public class LoadGameMessage extends ServerMessage {

    private final GameData game;

    /**
     * Constructs a LoadGameMessage with the specified game data.
     *
     * @param gameData The game data to include in the message.
     */
    public LoadGameMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        game = gameData;
    }

    /**
     * Retrieves the game data associated with this message.
     *
     * @return The game data.
     */
    public GameData getGameData() {
        return game;
    }

    @Override
    public String toString() {
        return "LoadGameMessage{" +
                "serverMessageType=" + getServerMessageType() +
                ", gameData=" + game.getGameName() +
                '}';
    }
}