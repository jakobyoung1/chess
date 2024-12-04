package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class ChessWebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ChessWebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinGame(message, session);
            case JOIN_OBSERVER -> joinObserver(message, session);
            case LEAVE -> leaveGame(message, session);
            case REDRAW -> redraw(message, session);
            case MAKE_MOVE -> makeMove(message, session);
            case RESIGN -> resign(message, session);

        }
    }

    public void joinGame(String action, Session session) {
        JoinPlayerCommand joinPlayer = new Gson().fromJson(action, JoinPlayerCommand.class);

        String authToken = joinPlayer.getAuthToken();
        ChessGame.TeamColor color = joinPlayer.getPlayerColor();
        int gameID = joinPlayer.getGameID();
        connections.add(gameID, authToken, session);
        try {

            GameData game = gameDAO.getGame(gameID);


            AuthData auth = authDAO.getAuth(authToken);
            String userName = auth.username();

            if (!Objects.equals(userName, game.getWhiteUsername()) && !Objects.equals(userName, game.getBlackUsername())){
                Error error = new Error("Need to call http first\n");
                connections.sendMessage(gameID, authToken, new Gson().toJson(error));
                return;
            }



            if (color != ChessGame.TeamColor.WHITE && color != ChessGame.TeamColor.BLACK) {
                Error error = new Error("No color\n");
                connections.sendMessage(gameID, authToken, new Gson().toJson(error));
                return;
            }


            if (game.getWhiteUsername() != null && color == ChessGame.TeamColor.WHITE && !(userName.equals(game.getWhiteUsername()))){
                Error error = new Error("Spot already taken\n");
                String message = new Gson().toJson(error);
                connections.sendMessage(gameID, authToken, message);
                return;
            }
            else if (game.getBlackUsername()!= null && color == ChessGame.TeamColor.BLACK && !(userName.equals(game.getBlackUsername()))){

                Error error = new Error("Spot already taken\n");
                connections.sendMessage(gameID, authToken, new Gson().toJson(error));
                return;
            }

            var message = String.format(userName + " has joined team " + color + "\n");
            var notification = new NotificationMessage(message);

            connections.add(gameID, authToken, session);
            connections.broadcast(authToken, notification, gameID);

            var loadGame = new LoadGameMessage(game);
            connections.sendMessage(gameID, authToken, new Gson().toJson(loadGame));
        } catch (Exception e) {
            Error error = new Error("Error");
            connections.sendMessage(gameID, authToken, new Gson().toJson(error));
        }


    }


    public void joinObserver(String action, Session session) {
        JoinObserverCommand joinObserver = new Gson().fromJson(action, JoinObserverCommand.class);
        String authToken = joinObserver.getAuthToken();
        int gameID = joinObserver.getGameID();
        connections.add(gameID, authToken, session);
        try {
            authenticateToken(authToken);
            GameData gameData = gameDAO.getGame(gameID);
            String userName = getUsername(authToken);

            var message = String.format(userName + " has joined as an observer\n");
            var notification = new NotificationMessage(message);
            connections.add(gameID, authToken, session);
            connections.broadcast(authToken, notification, gameID);

            var loadGame = new LoadGameMessage(gameData);
            connections.sendMessage(gameID, authToken, new Gson().toJson(loadGame));
        } catch (Exception ex) {
            Error error = new Error(ex.getMessage());
            System.out.println(ex.getMessage());
            String message = new Gson().toJson(error);
            connections.sendMessage(gameID, authToken, message);
        }

    }


    public void leaveGame(String action, Session session) {

        LeaveCommand leaveGame = new Gson().fromJson(action, LeaveCommand.class);
        String authToken = leaveGame.getAuthToken();
        int gameID = leaveGame.getGameID();
        String userName = getUsername(authToken);

        connections.remove(gameID, authToken);
        var message = String.format(userName + " has left the game\n");
        var notification = new NotificationMessage(message);
        connections.broadcast("", notification, gameID);

    }


    public void redraw(String action, Session session) {
        try {
            RedrawBoardCommand redraw = new Gson().fromJson(action, RedrawBoardCommand.class);
            String authToken = redraw.getAuthToken();
            int gameID = redraw.getGameID();
            GameDAO gameDAO = new GameDAO();
            GameData game = gameDAO.getGame(gameID);
            ChessGame.TeamColor color = getTeamColor(authToken, game);
            var loadGame = new LoadGameMessage(game);
            connections.sendMessage(gameID, authToken, new Gson().toJson(loadGame));
        } catch (Exception e) {
        }
    }


    public void makeMove(String action, Session session) {

        MakeMoveCommand makeMove = new Gson().fromJson(action, MakeMoveCommand.class);
        int gameID = makeMove.getGameID();
        ChessMove move = makeMove.getMove();
        String authToken = makeMove.getAuthToken();
        try {
            GameDAO gameDAO = new GameDAO();
            GameData game = gameDAO.getGame(gameID);
            ChessGame.TeamColor color = null;

            AuthData auth = authDAO.getAuth(authToken);
            String username = auth.username();

//            if (game.getGame().isGameOver){
//                throw new Exception("Game is already over. Cannot make another move\n");
//            }

            if (Objects.equals(game.getBlackUsername(), username)) {
                color = ChessGame.TeamColor.BLACK;
            }
            else if (Objects.equals(game.getWhiteUsername(), username)) {
                color = ChessGame.TeamColor.WHITE;
            }
            else {
                Error error = new Error("Error: Not one of the teams\n");
                String message = new Gson().toJson(error);
                connections.sendMessage(gameID, authToken, message);
                return;
            }

            if (color != game.getGame().getTeamTurn()) {
                Error error = new Error("Error: Not your turn\n");
                String message = new Gson().toJson(error);
                connections.sendMessage(gameID, authToken, message);
                return;
            }

            game.getGame().makeMove(move);
            gameDAO.updateGame(gameID, game);



            LoadGameMessage newGame = new LoadGameMessage(game);
            connections.broadcast("", newGame, gameID);
            String message = (username + " has made a move: " + move + "\n");
            NotificationMessage serverMessage = new NotificationMessage(message);
            connections.broadcast(authToken, serverMessage, gameID);
            color = ChessGame.TeamColor.BLACK;

            var turnColor = game.getGame().getTeamTurn();
            if (game.getGame().isInCheck(turnColor) && !game.getGame().isInCheckmate(turnColor)){
                String turnUsername = game.getBlackUsername();
                if (turnColor == ChessGame.TeamColor.WHITE){
                    turnUsername = game.getWhiteUsername();
                }
                String newMessage = (turnUsername + " is in check\n");
                NotificationMessage Message = new NotificationMessage(newMessage);
                connections.broadcast("", Message, gameID);
            }


            for (int i = 0; i < 2; i++) {
                if (game.getGame().isInCheckmate(color)) {
                    //game.getGame().isGameOver = true;
                    gameDAO.updateGame(gameID, game);
                    NotificationMessage notification = new NotificationMessage(color.toString() + " is in checkmate. GAME OVER\n");
                    connections.broadcast("", notification, gameID);
                }
                color = ChessGame.TeamColor.WHITE;
            }

        } catch (InvalidMoveException e) {
            Error error = new Error("Invalid Move\n");
            String message = new Gson().toJson(error);
            connections.sendMessage(gameID, authToken, message);
        } catch (Exception ex) {
            Error error = new Error(ex.getMessage());
            System.out.println(ex.getMessage());
            String message = new Gson().toJson(error);
            connections.sendMessage(gameID, authToken, message);
        }
    }

    public void resign(String action, Session session) {
        ResignCommand resign = new Gson().fromJson(action, ResignCommand.class);
        int gameID = resign.getGameID();
        String authToken = resign.getAuthToken();

        try {
            AuthData auth = authDAO.getAuth(authToken);
            String username = auth.username();

            GameData game = gameDAO.getGame(gameID);
//            if(game.getGame().isGameOver) {
//                throw new Exception("Game is already over\n");
//            } else
            if (!Objects.equals(username, game.getBlackUsername()) && !Objects.equals(username, game.getWhiteUsername())){
                throw new Exception("Observers cannot resign game\n");
            }

            //game.getGame().isGameOver = true;
            gameDAO.updateGame(gameID, game);


            String message = String.format("%s has resigned.\n GAME OVER\n", username);
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast("", notification, gameID);

        } catch (Exception ex) {
            Error error = new Error(ex.getMessage());
            System.out.println(ex.getMessage());
            String message = new Gson().toJson(error);
            connections.sendMessage(gameID, authToken, message);
        }

    }

    private String getUsername(String authToken) {
        try {
            AuthDAO authDAO = new AuthDAO();
            AuthData authData = authDAO.getAuth(authToken);
            return authData.username();
        } catch (DataAccessException ex) {
            return "";
        }

    }

    private ChessGame.TeamColor getTeamColor(String authToken, GameData gameData){
        String username = getUsername(authToken);
        if (gameData.getBlackUsername() == username) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void authenticateToken(String authToken) throws DataAccessException {
        AuthDAO authDAO = new AuthDAO();
        authDAO.getAuth(authToken);
    }
}