package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {

    private final Integer gameID;
    private final ChessGame.TeamColor color;

    public ConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor color) {
        // Call the parent constructor with the command type and auth token
        super(CommandType.CONNECT, authToken);
        if (gameID == null) {
            throw new IllegalArgumentException("GameID cannot be null.");
        }
        this.color = color;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    // Getter for game ID
    public Integer getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return "ConnectCommand{" +
                "gameID=" + gameID +
                ", authToken='" + getAuthToken() + '\'' +
                ", commandType=" + getCommandType() +
                '}';
    }
}