package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {

    private final Integer gameID; // Unique identifier for the game
    private final ChessGame.TeamColor playerColor; // Player's chosen color

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        // Call the parent constructor with required parameters
        super(CommandType.JOIN_PLAYER, authToken);
        if (gameID == null) {
            throw new IllegalArgumentException("GameID cannot be null.");
        }
        if (playerColor == null) {
            throw new IllegalArgumentException("PlayerColor cannot be null.");
        }
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    // Getter for game ID
    public Integer getGameID() {
        return gameID;
    }

    // Getter for player color
    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    @Override
    public String toString() {
        return "JoinPlayer{" +
                "gameID=" + gameID +
                ", playerColor=" + playerColor +
                ", authToken='" + getAuthToken() + '\'' +
                ", commandType=" + getCommandType() +
                '}';
    }
}