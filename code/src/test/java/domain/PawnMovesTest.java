//package domain;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.function.Executable;
//
//public class PawnMovesTest {
//
//    private ChessPlayer playerOne;
//    private ChessPlayer playerTwo;
//
//    private ChessGame Game;
//
//    // ----------------------- CREATE MAIN GAME ----------------------- //
//
//    @BeforeEach
//    public void setUp(){
//        //CREATE GAME
//        playerOne = new ChessPlayer("WHITE", "white@");
//
//        playerTwo = new ChessPlayer("BLACK", "black@");
//
//        Date d = new Date(System.currentTimeMillis());
//
//        this.Game = new ChessGame(playerOne, playerTwo, d);
//    }
//
//    // ----------------------- AUXILIAR BOARDS ----------------------- //
//    //
//    //NOTES FOR setPieces FUNCTION:
//    //
//    //setPieces([[PIECE, COLOR, ROW, COL], [PIECE, COLOR, ROW, COL], etc...])
//    //
//    //PIECES: 0 - KING; 1 - QUEEN; 2 - ROOK; 3 - BISHOP; 4 - KNIGHT; 5 - PAWN
//    //COLORS: 0 - WHITE; 1 - BLACK
//    //ROW: BETWEEN 0 AND 7
//    //COL: BETWEEN 0 AND 7
//
//    public void onePawnEach(){
//        ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
//
//        ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
//        kingPieceW.add(0); kingPieceW.add(0); kingPieceW.add(0); kingPieceW.add(4);
//        ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
//        kingPieceB.add(0); kingPieceB.add(1); kingPieceB.add(7); kingPieceB.add(4);
//        ArrayList<Integer> pawnPieceW = new ArrayList<Integer>();
//        pawnPieceW.add(5); pawnPieceW.add(0); pawnPieceW.add(1); pawnPieceW.add(4);
//        ArrayList<Integer> pawnPieceB = new ArrayList<Integer>();
//        pawnPieceB.add(5); pawnPieceB.add(1); pawnPieceB.add(6); pawnPieceB.add(3);
//
//        newBoardConfig.add(kingPieceW);
//        newBoardConfig.add(kingPieceB);
//        newBoardConfig.add(pawnPieceW);
//        newBoardConfig.add(pawnPieceB);
//
//        this.Game.getBoard().setPieces(newBoardConfig);
//    }
//
//    public void preEnPassant(){
//        ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
//
//        ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
//        kingPieceW.add(0); kingPieceW.add(0); kingPieceW.add(0); kingPieceW.add(4);
//        ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
//        kingPieceB.add(0); kingPieceB.add(1); kingPieceB.add(7); kingPieceB.add(4);
//        ArrayList<Integer> pawnPieceW = new ArrayList<Integer>();
//        pawnPieceW.add(5); pawnPieceW.add(0); pawnPieceW.add(4); pawnPieceW.add(4);
//        ArrayList<Integer> pawnPieceB = new ArrayList<Integer>();
//        pawnPieceB.add(5); pawnPieceB.add(1); pawnPieceB.add(4); pawnPieceB.add(3);
//
//        newBoardConfig.add(kingPieceW);
//        newBoardConfig.add(kingPieceB);
//        newBoardConfig.add(pawnPieceW);
//        newBoardConfig.add(pawnPieceB);
//
//        this.Game.getBoard().setPieces(newBoardConfig);
//    }
//
//    public void prePromotion(){
//        ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
//
//        ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
//        kingPieceW.add(0); kingPieceW.add(0); kingPieceW.add(0); kingPieceW.add(0);
//        ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
//        kingPieceB.add(0); kingPieceB.add(1); kingPieceB.add(7); kingPieceB.add(7);
//        ArrayList<Integer> pawnPieceW = new ArrayList<Integer>();
//        pawnPieceW.add(5); pawnPieceW.add(0); pawnPieceW.add(4); pawnPieceW.add(6);
//
//        newBoardConfig.add(kingPieceW);
//        newBoardConfig.add(kingPieceB);
//        newBoardConfig.add(pawnPieceW);
//
//        this.Game.getBoard().setPieces(newBoardConfig);
//    }
//
//    // ----------------------- TESTS ZONE ----------------------- //
//
//    // -----> Pawn goes two positions ahead <----- //
//    @Order(1)
//    @Test
//    public void moveTwoAhead(){
//
//        Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
//
//            public void execute() throws IllegalMoveException {
//                onePawnEach();
//                int timeMilli = 0;
//                ChessPosition mFrom = new ChessPosition(1, 4);
//                ChessPosition mTo = new ChessPosition(3, 4);
//
//                ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(1, 4), mFrom, mTo, timeMilli);
//
//                Game.addMove(mv);
//            }
//
//        });
//
//        //System.out.println(exception.getMessage());
//
////        assertEquals("Move out of bounds", exception.getMessage());
//    }
//
////    // -----> Pawn goes two positions ahead <----- //
////    @Order(1)
////    @Test
////    public void moveTwoAhead(){
////
////        Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
////
////            public void execute() throws IllegalMoveException {
////                onePawnEach();
////                int timeMilli = 0;
////                ChessPosition mFrom = new ChessPosition(1, 4);
////                ChessPosition mTo = new ChessPosition(3, 4);
////
////                ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(1, 4), mFrom, mTo, timeMilli);
////
////                Game.addMove(mv);
////            }
////
////        });
//
//        //System.out.println(exception.getMessage());
//
////        assertEquals("Move out of bounds", exception.getMessage());
//    }
//}
