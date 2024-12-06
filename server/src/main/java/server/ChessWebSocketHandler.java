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
        System.out.println("MESSAGE RECEIVED BY HANDLER: " + message);
        UserGameCommand action = UserGameCommand.fromJson(message);

        switch (action.getCommandType()) {
            case CONNECT -> connect(message, session);
            case LEAVE -> leaveGame(message, session);
            case REDRAW -> redraw(message, session);
            case MAKE_MOVE -> makeMove(message, session);
            case RESIGN -> resign(message, session);
        }
    }

    public void connect(String action, Session session) {
        ConnectCommand connectCommand = new Gson().fromJson(action, ConnectCommand.class);

        String authToken = connectCommand.getAuthToken();
        Integer gameID = connectCommand.getGameID();
        ChessGame.TeamColor color = connectCommand.getColor();

        connections.add(gameID, authToken, session);
        System.out.println(connections);

        try {
            GameData game = gameDAO.getGame(gameID);
            AuthData auth = authDAO.getAuth(authToken);
            String username = auth.username();

            if (color == null) {
                String observerMessage = String.format("%s has joined as an observer\n", username);
                NotificationMessage notification = new NotificationMessage(observerMessage);

                connections.broadcast(authToken, notification, gameID);

                LoadGameMessage loadGame = new LoadGameMessage(game);
                connections.sendMessage(gameID, authToken, new Gson().toJson(loadGame));
                return;
            }

            String playerMessage = String.format("%s has joined team %s\n", username, color);
            NotificationMessage notification = new NotificationMessage(playerMessage);
            connections.broadcast(authToken, notification, gameID);

            LoadGameMessage loadGame = new LoadGameMessage(game);
            connections.sendMessage(gameID, authToken, new Gson().toJson(loadGame));
        } catch (DataAccessException e) {
            // Send an error message if there is an issue accessing the database
            ErrorMessage errorMessage = new ErrorMessage("Error accessing game data: " + e.getMessage());
            connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
            System.out.println(e);
        } catch (Exception e) {
            // Send a generic error message for any other exceptions
            ErrorMessage errorMessage = new ErrorMessage("Error connecting to the game: " + e.getMessage());
            connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
            System.out.println(e);
        }
    }

    public void leaveGame(String action, Session session) {
        LeaveCommand leaveGame = new Gson().fromJson(action, LeaveCommand.class);
        String authToken = leaveGame.getAuthToken();
        int gameID = leaveGame.getGameID();

        try {
            // Get the username and game data
            String username = getUsername(authToken);
            GameData game = gameDAO.getGame(gameID);

            if (game == null) {
                throw new Exception("Game not found.");
            }

            // Remove the player from the appropriate team
            if (username.equals(game.getWhiteUsername())) {
                game.setWhiteUsername(null); // Free up the White team
            } else if (username.equals(game.getBlackUsername())) {
                game.setBlackUsername(null); // Free up the Black team
            }

            // Update the game in the database
            gameDAO.updateGame(game.getGameId(), game);

            // Remove the player's connection
            connections.remove(gameID, authToken);

            // Broadcast the leave message to other players and observers
            String leaveMessage = String.format("%s has left the game\n", username);
            NotificationMessage notification = new NotificationMessage(leaveMessage);
            connections.broadcast("", notification, gameID);
        } catch (Exception e) {
            // Handle errors gracefully
            ErrorMessage errorMessage = new ErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "An error occurred while leaving the game."
            );
            try {
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } catch (IOException ioException) {
                System.err.println("Error sending message to session: " + ioException.getMessage());
            }
            System.err.println("Error in leaveGame: " + e.getMessage());
        }
    }

    public void redraw(String action, Session session) {
        try {
            RedrawBoardCommand redraw = new Gson().fromJson(action, RedrawBoardCommand.class);
            String authToken = redraw.getAuthToken();
            int gameID = redraw.getGameID();
            GameData game = gameDAO.getGame(gameID);
            LoadGameMessage loadGame = new LoadGameMessage(game);
            connections.sendMessage(gameID, authToken, new Gson().toJson(loadGame));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeMove(String action, Session session) {
        MakeMoveCommand makeMove = new Gson().fromJson(action, MakeMoveCommand.class);
        int gameID = makeMove.getGameID();
        ChessMove move = makeMove.getMove();
        String authToken = makeMove.getAuthToken();
        try {
            // Validate the authentication token
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                // Send an error message directly to the session
                ErrorMessage errorMessage = new ErrorMessage("Invalid or expired authentication token.");
                try {
                    session.getRemote().sendString(new Gson().toJson(errorMessage));
                } catch (IOException e) {
                    System.err.println("Error sending message to session: " + e.getMessage());
                }
                return;
            }
            // Fetch game data
            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                ErrorMessage errorMessage = new ErrorMessage("Game not found for ID: " + gameID);
                connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
                return;
            }
            // Check if the game is already over
            if (game.getGame().isGameOver()) {
                ErrorMessage errorMessage = new ErrorMessage("The game is already over. No further moves can be made.");
                try {
                    session.getRemote().sendString(new Gson().toJson(errorMessage));
                } catch (IOException e) {
                    System.err.println("Error sending game over message: " + e.getMessage());
                }
                return;
            }
            // Get username and team color of the player making the move
            String username = getUsername(authToken);
            ChessGame.TeamColor color = Objects.equals(username, game.getBlackUsername()) ?
                    ChessGame.TeamColor.BLACK : Objects.equals(username, game.getWhiteUsername()) ?
                    ChessGame.TeamColor.WHITE : null;
            // Check if the user is an observer (no team color)
            if (color == null) {
                ErrorMessage errorMessage = new ErrorMessage("Observers cannot make moves.");
                connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
                return;
            }
            // Check if either player has resigned
            if (game.getGame().isBlackResigned() || game.getGame().isWhiteResigned()) {
                ErrorMessage errorMessage = new ErrorMessage("Game over due to resignation.");
                connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
                return;
            }
            // Check if it's the player's turn
            if (color != game.getGame().getTeamTurn()) {
                ErrorMessage errorMessage = new ErrorMessage("It is not your turn.");
                connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
                return;
            }
            // Attempt to make the move
            game.getGame().makeMove(move);
            gameDAO.updateGame(gameID, game);
            // Notify all other players and observers about the move
            String moveMessage = String.format("%s (%s) has made a move: %s\n", username, color, move);
            NotificationMessage notification = new NotificationMessage(moveMessage);
            connections.broadcast(authToken, notification, gameID); // Broadcast to all except the player who made the move
            // Send the updated game state to all players and observers
            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            connections.broadcast("", loadGameMessage, gameID); // Broadcast the updated game state to everyone
            // Check for game-ending conditions
            ChessGame.TeamColor opponentColor = (color == ChessGame.TeamColor.BLACK) ?
                    ChessGame.TeamColor.WHITE :
                    ChessGame.TeamColor.BLACK;
            if (game.getGame().isInCheckmate(opponentColor)) {
                // Opponent is in checkmate
                String gameOverMessage = opponentColor + " is in checkmate. GAME OVER\n";
                NotificationMessage gameOverNotification = new NotificationMessage(gameOverMessage);
                connections.broadcast("", gameOverNotification, gameID);
                // Mark the game as over and persist the state
                game.getGame().setGameOver(true);
                gameDAO.updateGame(gameID, game);
            } else if (game.getGame().isInCheck(opponentColor)) {
                String gameOverMessage = opponentColor + " is in check. \n";
                NotificationMessage gameOverNotification = new NotificationMessage(gameOverMessage);
                connections.broadcast("", gameOverNotification, gameID);
                gameDAO.updateGame(gameID, game);
            } else if (game.getGame().isInStalemate(opponentColor)) {
                // Game is in stalemate
                String gameOverMessage = " STALEMATE. GAME OVER\n";
                NotificationMessage gameOverNotification = new NotificationMessage(gameOverMessage);
                connections.broadcast("", gameOverNotification, gameID);
                // Mark the game as over and persist the state
                game.getGame().setGameOver(true);
                gameDAO.updateGame(gameID, game);
            }
        } catch (InvalidMoveException e) {
            // Handle invalid moves
            ErrorMessage errorMessage = new ErrorMessage("Invalid move: " + e.getMessage());
            connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
        } catch (Exception e) {
            // Handle generic exceptions
            ErrorMessage errorMessage = new ErrorMessage("An error occurred while making the move.");
            connections.sendMessage(gameID, authToken, new Gson().toJson(errorMessage));
            System.err.println("Error in makeMove: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resign(String action, Session session) {
        ResignCommand resign = new Gson().fromJson(action, ResignCommand.class);
        int gameID = resign.getGameID();
        String authToken = resign.getAuthToken();

        try {
            String username = getUsername(authToken);
            GameData game = gameDAO.getGame(gameID);

            if (!Objects.equals(username, game.getBlackUsername()) && !Objects.equals(username, game.getWhiteUsername())) {
                throw new Exception("Observers cannot resign the game.");
            }

            // Check if the game is already over due to a previous resignation
            if (game.getGame().isBlackResigned() || game.getGame().isWhiteResigned()) {
                throw new Exception("The game is already over due to a previous resignation.");
            } else {
                // Set resign status based on the player color
                if (Objects.equals(username, game.getBlackUsername())) {
                    game.getGame().setBlackResigned(true); // Mark Black as resigned
                } else if (Objects.equals(username, game.getWhiteUsername())) {
                    game.getGame().setWhiteResigned(true); // Mark White as resigned
                }

                gameDAO.updateGame(game.getGameId(), game);

                System.out.println("You have resigned from the game.");
                String resignMessage = String.format("%s has resigned. GAME OVER\n", username);
                NotificationMessage notification = new NotificationMessage(resignMessage);
                connections.broadcast("", notification, gameID);
            }


        } catch (Exception e) {
            // Handle errors gracefully
            ErrorMessage errorMessage = new ErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "An error occurred while processing the resignation."
            );
            try {
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } catch (IOException ioException) {
                System.err.println("Error sending message to session: " + ioException.getMessage());
            }
            System.err.println("Error in resign: " + e.getMessage());
        }
    }


    private String getUsername(String authToken) {
        try {
            AuthData auth = authDAO.getAuth(authToken);
            return auth.username();
        } catch (DataAccessException e) {
            return "";
        }
    }
}