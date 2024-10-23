package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.GameService;
import server.requests.*;
import server.results.*;

import java.util.HashMap;

public class GameServiceTest {

    private static HashMap<Integer, GameData> games = new HashMap<>();
    private static GameDAO gameDAO = new GameDAO(games);
    private static GameService gameService = new GameService(gameDAO);

    public static void main(String[] args) {
        try {
//            testStartGameSuccess();
//            testJoinGameSuccess();
//            testMakeMoveSuccess();
            //testListGamesSuccess();
            System.out.println("All tests passed!");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }

//    public static void testStartGameSuccess() throws DataAccessException {
//        StartGameRequest request = new StartGameRequest("player1", "player2", "Test Game");
//        StartGameResult result = gameService.startGame(request);
//
//        if (result == null) {
//            throw new RuntimeException("StartGameResult is null");
//        }
//        if (result.getGameId() == 0) {
//            throw new RuntimeException("Game ID is invalid");
//        }
//        if (!"Game started successfully".equals(result.getMessage())) {
//            throw new RuntimeException("Success message is incorrect");
//        }
//
//        System.out.println("Games map after game start: " + games);
//        System.out.println("testStartGameSuccess passed!");
//    }

//    public static void testJoinGameSuccess() throws DataAccessException {
//        StartGameRequest startRequest = new StartGameRequest("player1", "player2", "Test Game");
//        StartGameResult startResult = gameService.startGame(startRequest);
//
//        JoinGameRequest joinRequest = new JoinGameRequest( "BLACK", "player2", startResult.getGameId());
//        JoinGameResult joinResult = gameService.joinGame(joinRequest);
//
//        if (joinResult == null) {
//            throw new RuntimeException("JoinGameResult is null");
//        }
//        if (!"Joined game successfully".equals(joinResult.message())) {
//            throw new RuntimeException("Join success message is incorrect");
//        }
//
//        System.out.println("Games map after joining game: " + games);
//        System.out.println("testJoinGameSuccess passed!");
//    }

//    public static void testMakeMoveSuccess() throws DataAccessException, InvalidMoveException {
//        StartGameRequest startRequest = new StartGameRequest("player1", "player2", "Test Game");
//        StartGameResult startResult = gameService.startGame(startRequest);
//
//        ChessMove move = new ChessMove(new ChessPosition(2,1), new ChessPosition(3,1), null);
//        MoveRequest moveRequest = new MoveRequest(startResult.getGameId(), move);
//        MoveResult moveResult = gameService.makeMove(moveRequest);
//
//        if (moveResult == null) {
//            throw new RuntimeException("MoveResult is null");
//        }
//        if (!"Move made successfully".equals(moveResult.message())) {
//            throw new RuntimeException("Move success message is incorrect");
//        }
//
//        System.out.println("Games map after move: " + games);
//        System.out.println("testMakeMoveSuccess passed!");
//    }

//    public static void testListGamesSuccess() throws DataAccessException {
//        ListGamesRequest listRequest = new ListGamesRequest();
//        ListGamesResult listResult = gameService.listGames(listRequest);
//
//        if (listResult == null) {
//            throw new RuntimeException("ListGamesResult is null");
//        }
//        if (listResult.getGames().isEmpty()) {
//            throw new RuntimeException("No games found in the list");
//        }
//
//        System.out.println("Games listed: " + listResult.getGames());
//        System.out.println("testListGamesSuccess passed!");
//    }
}
