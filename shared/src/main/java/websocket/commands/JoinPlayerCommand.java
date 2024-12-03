package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {

    private final Integer gameID; // Unique identifier for the game
    private final ChessGame.TeamColor playerColor; // Player's chosen color

    /**
     * Constructor for JoinPlayerCommand.
     *
     * @param authToken   The authentication token for the player.
     * @param gameID      The ID of the game to join.
     * @param playerColor The color the player chooses (WHITE or BLACK).
     */
    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        // Call the parent constructor with required parameters
        super(CommandType.JOIN_PLAYER, authToken);

        // Validate input parameters
        if (gameID == null) {
            throw new IllegalArgumentException("GameID cannot be null.");
        }
        this.gameID = gameID;

        // PlayerColor can be null for observers
        this.playerColor = playerColor;
    }

    /**
     * Gets the ID of the game.
     *
     * @return The game ID.
     */
    public Integer getGameID() {
        return gameID;
    }

    /**
     * Gets the player's chosen color.
     *
     * @return The player's chosen color, or null if not applicable (e.g., for observers).
     */
    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    @Override
    public String toString() {
        return "JoinPlayerCommand{" +
                "gameID=" + gameID +
                ", playerColor=" + (playerColor != null ? playerColor : "None (Observer)") +
                ", authToken='" + getAuthToken() + '\'' +
                ", commandType=" + getCommandType() +
                '}';
    }
}