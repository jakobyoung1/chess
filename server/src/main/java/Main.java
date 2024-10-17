import chess.*;
import server.Server;

public class Main {
//    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
//    }

    public static void main(String[] args) {
        Server s = new Server();
        int port = s.run(8080);
    }
}