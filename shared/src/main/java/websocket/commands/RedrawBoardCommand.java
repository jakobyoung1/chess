package websocket.commands;

public class RedrawBoardCommand extends UserGameCommand {
    private int gameID;

    public RedrawBoardCommand(String authToken, int gameID) {
        super(CommandType.REDRAW, authToken);
    }

    public int getGameID(){
        return gameID;
    }
}