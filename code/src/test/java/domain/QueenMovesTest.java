package domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.ArrayList;
import java.util.Date;

class QueenMovesTest {

	private ChessPlayer playerOne;
	private ChessPlayer playerTwo;
	private ChessGame Game;


	// ----------------------- CREATE MAIN GAME ----------------------- //
	@BeforeEach
	public void setUp(){
		//CREATE GAME
		playerOne = new ChessPlayer("WHITE", "white@");	
	
		playerTwo = new ChessPlayer("BLACK", "black@");
		
		Date d = new Date(System.currentTimeMillis());
		
		this.Game = new ChessGame(playerOne, playerTwo, d);
	}
	
	
	// ----------------------- AUXILIAR BOARDS ----------------------- //
	//
	//NOTES FOR setPieces FUNCTION:
	//
	//setPieces([[PIECE, COLOR, ROW, COL], [PIECE, COLOR, ROW, COL], etc...])
	//
	//PIECES: 0 - KING; 1 - QUEEN; 2 - ROOK; 3 - BISHOP; 4 - KNIGHT; 5 - PAWN
	//COLORS: 0 - WHITE; 1 - BLACK
	//ROW: BETWEEN 0 AND 7
	//COL: BETWEEN 0 AND 7
	public void SituationKingInCheck(){
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(4);
		
		ArrayList<Integer> queenPieceW = new ArrayList<Integer>();
		queenPieceW.add(1);queenPieceW.add(0);queenPieceW.add(1);queenPieceW.add(4);
		
		ArrayList<Integer> rookPieceB = new ArrayList<Integer>();
		rookPieceB.add(2);rookPieceB.add(1);rookPieceB.add(4);rookPieceB.add(4);
		
		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(queenPieceW);
		newBoardConfig.add(rookPieceB);
		
		this.Game.getBoard().setPieces(newBoardConfig);
	}
	
	public void SituationQueenWithManyChoices(){
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(5);kingPieceW.add(0);

		ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
		kingPieceB.add(0);kingPieceB.add(1);kingPieceB.add(5);kingPieceB.add(5);
		
		ArrayList<Integer> queenPieceW = new ArrayList<Integer>();
		queenPieceW.add(1);queenPieceW.add(0);queenPieceW.add(0);queenPieceW.add(3);
		
		ArrayList<Integer> pawnPieceW = new ArrayList<Integer>();
		pawnPieceW.add(5);pawnPieceW.add(0);pawnPieceW.add(1);pawnPieceW.add(3);
		
		ArrayList<Integer> bishopPieceB = new ArrayList<Integer>();
		bishopPieceB.add(3);bishopPieceB.add(1);bishopPieceB.add(4);bishopPieceB.add(7);
		
		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(kingPieceB);
		newBoardConfig.add(queenPieceW);
		newBoardConfig.add(pawnPieceW);
		newBoardConfig.add(bishopPieceB);
		
		this.Game.getBoard().setPieces(newBoardConfig);
	}


	// ----------------------- TESTS ZONE ----------------------- //
	
	// -----> KING STAY IN CHECK <----- //
	@Order(1)
	@Test
	public void QueenMoveKingInCheck(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				
				SituationKingInCheck();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(1, 4);
				ChessPosition mTo = new ChessPosition(2,3);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(1, 4), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("KING will be in 'CHECK'", exception.getMessage());
	}	


	// -----> QUEEN DOESN'T MOVE THIS WAY <----- //
	@Order(2)
	@Test
	public void QueenMoveWrongWay(){
		
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				
				SituationQueenWithManyChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(0, 3);
				ChessPosition mTo = new ChessPosition(2,2);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0,3), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});
		assertEquals("QUEEN doesn't move that way", exception.getMessage());
	}


	// -----> QUEEN HAVE A PIECE IN HER WAY <----- //
	@Order(2)
	@Test
	public void QueenMovePieceBlocking(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				
				SituationQueenWithManyChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(0, 3);
				ChessPosition mTo = new ChessPosition(2,3);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0,3), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("You have a piece in your way", exception.getMessage());
	}


	// -----> QUEEN CAN'T CAPTURE OWN PIECES <----- //
	@Order(3)
	@Test
	public void QueenMoveCaptureOwnPiece(){
		
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				
				SituationQueenWithManyChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(0, 3);
				ChessPosition mTo = new ChessPosition(1,3);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0,3), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("Can't capture your own pieces", exception.getMessage());
	}


	// -----> QUEEN MOVE OUT OF BOUNDS <----- //
	@Order(4)
	@Test
	public void QueenMoveOutOfBoard(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				
				SituationQueenWithManyChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(0, 3);
				ChessPosition mTo = new ChessPosition(-1,3);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0,3), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("Move out of bounds", exception.getMessage());
	}


	// -----> KING STAY IN SAME POSITION <----- //
	@Order(6)
	@Test
	public void QueenMoveToSamePosition(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {
				SituationQueenWithManyChoices();

				int timeMilli = 0;

				//JOGADA DO REI
				ChessPosition mFrom = new ChessPosition(0, 3);
				ChessPosition mTo = new ChessPosition(0,3);
				
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 3), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("Move out of bounds", exception.getMessage());
	}


	// -----> KING STAY IN CHECK <----- //
	@Order(7)
	@Test
	public void QueenCaptureOpponent(){
			
		ChessPiece queen;
		
		SituationQueenWithManyChoices();
		
		queen = Game.getBoard().get(0, 3);

		int timeMilli = 0;

		//JOGADA DO REI
		ChessPosition mFrom = new ChessPosition(0, 3);
		ChessPosition mTo = new ChessPosition(4, 7);
		
		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 3), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			
			System.out.println(exception.getMessage());
		}
		assertEquals(Game.getBoard().get(4, 7), queen);
	}


//	 -----> KING STAY IN CHECK <----- //
	@Order(7)
	@Test
	public void QueenMoveTheRightWay(){

		ChessPiece queen;
		int timeMilli = 0;

		SituationQueenWithManyChoices();

		queen = Game.getBoard().get(0, 3);

		//JOGADA DO REI
		ChessPosition mFrom = new ChessPosition(0, 3);
		ChessPosition mTo = new ChessPosition(2,1);

		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 3), mFrom, mTo, timeMilli);

		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
		}
		assertEquals(Game.getBoard().get(2,1), queen);
	}

	//	 -----> Queen Try's to Jump Over Other Piece <----- //
	@Order(8)
	@Test
	public void QueenTryToJumpOver(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "You have a piece in your way";

		SituationQueenWithManyChoices();
		ChessPosition mFrom = new ChessPosition(0, 3);
		ChessPosition mTo = new ChessPosition(2,3);

		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 3), mFrom, mTo, timeMilli);

		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}

	//	 -----> Queen Try's to Capture Own Piece <----- //
	@Order(9)
	@Test
	public void QueenTryToEatOwn(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "Can't capture your own pieces";

		SituationQueenWithManyChoices();
		ChessPosition mFrom = new ChessPosition(0, 3);
		ChessPosition mTo = new ChessPosition(1,3);

		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 3), mFrom, mTo, timeMilli);

		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}
}
