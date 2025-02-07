package domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.ArrayList;
import java.util.Date;

class RookMovesTest {
	private ChessPlayer playerOne;
	private ChessPlayer playerTwo;
	private ChessGame Game;
	
	// ----------------------- CREATE MAIN GAME ----------------------- //
	@BeforeEach
	public void setUp(){
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
	
	public void SituationMultipleChoices(){
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(5);

		ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
		kingPieceB.add(0);kingPieceB.add(1);kingPieceB.add(7);kingPieceB.add(7);
		
		ArrayList<Integer> rookPieceW = new ArrayList<Integer>();
		rookPieceW.add(2);rookPieceW.add(0);rookPieceW.add(2);rookPieceW.add(5);
		
		ArrayList<Integer> pawnPieceW = new ArrayList<Integer>();
		pawnPieceW.add(5);pawnPieceW.add(0);pawnPieceW.add(2);pawnPieceW.add(6);
		
		ArrayList<Integer> rookPieceB = new ArrayList<Integer>();
		rookPieceB.add(2);rookPieceB.add(1);rookPieceB.add(7);rookPieceB.add(5);
		
		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(kingPieceB);
		newBoardConfig.add(rookPieceW);
		newBoardConfig.add(pawnPieceW);
		newBoardConfig.add(rookPieceB);
		
		this.Game.getBoard().setPieces(newBoardConfig);
	}

	// ----------------------- TESTS ZONE ----------------------- //
	
	// -----> KING STAY IN CHECK <----- //
	@Order(1)
	@Test
	public void RookMoveKingInCheck(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {
				
				SituationMultipleChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(2,5);
				ChessPosition mTo = new ChessPosition(2,1);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2,5), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});
		assertEquals("KING will be in 'CHECK'", exception.getMessage());
	}	

	// -----> ROOK MOVE WRONG WAY <----- //
	@Order(2)
	@Test
	public void RookMoveWrongWay(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {
				
				SituationMultipleChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(2,5);
				ChessPosition mTo = new ChessPosition(4,3);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2,5), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("ROOK doesn't move that way", exception.getMessage());
	}
	
	// -----> ROOK MOVE WITH A PIECE BLOCKING IT <----- //
	@Order(3)
	@Test
	public void RookMovePieceBlocking(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {
				
				SituationMultipleChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(2,5);
				ChessPosition mTo = new ChessPosition(2,7);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2,5), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("You have a piece in your way", exception.getMessage());
	}	

	// -----> ROOK CAN'T CAPTURE OWN PIECE <----- //
	@Order(4)
	@Test
	public void RookMoveCaptureOwnPiece(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {

				SituationMultipleChoices();

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(2,5);
				ChessPosition mTo = new ChessPosition(2,6);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2,5), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("Can't capture your own pieces", exception.getMessage());
	}	

	// -----> ROOK MOVE OUT OF BOUNDS <----- //
	@Order(5)
	@Test
	public void RookMoveOutOfBoard(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {
				
				SituationMultipleChoices();
				
				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(2,5);
				ChessPosition mTo = new ChessPosition(2,-1);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2,5), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("Move out of bounds", exception.getMessage());
	}	
	
	// -----> ROOK MOVE OUT OF BOUNDS <----- //
	@Order(5)
	@Test
	public void RookMoveToSamePosition(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			public void execute() throws IllegalMoveException {
				
				SituationMultipleChoices();
				
				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(2,5);
				ChessPosition mTo = new ChessPosition(2,5);
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2,5), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
		});
		assertEquals("Move out of bounds", exception.getMessage());
	}	

	// -----> ROOK MOVE CAPTURE OPPONENT <----- //
	@Order(6)
	@Test
	public void RookMoveCaptureOpponent(){
		ChessPiece rook;
		
		SituationMultipleChoices();
		
		rook = Game.getBoard().get(2, 5);
		
		int timeMilli = 0;

		//JOGADA DO REI
		ChessPosition mFrom = new ChessPosition(2,5);
		ChessPosition mTo = new ChessPosition(7,5);
		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2, 5), mFrom, mTo, timeMilli);

		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
		}
		assertEquals(Game.getBoard().get(7,5), rook);
	}
	
	// -----> ROOK MOVE CAPTURE OPPONENT <----- //
	@Order(7)
	@Test
	public void RookMoveTheRightWay(){
		ChessPiece rook;
		
		SituationMultipleChoices();
		
		rook = Game.getBoard().get(2, 5);

		int timeMilli = 0;

		//JOGADA DO REI
		ChessPosition mFrom = new ChessPosition(2,5);
		ChessPosition mTo = new ChessPosition(3,5);

		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(2, 5), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
		}
		assertEquals(Game.getBoard().get(3,5), rook);
	}

	// -----> ROOK Diagonal <----- //
	@Order(8)
	@Test
	public void RookMoveDiagonal(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "ROOK doesn't move that way";

		SituationMultipleChoices();

		ChessPosition mFrom = new ChessPosition(2, 5);
		ChessPosition mTo = new ChessPosition(3,6);

		ChessMove mv = new ChessMove(playerTwo, Game.getBoard().get(2, 5), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}

	// -----> ROOK Try's to Capture Own Piece <----- //
	@Order(9)
	@Test
	public void RookTryToEatOwn(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "Can't capture your own pieces";

		SituationMultipleChoices();

		ChessPosition mFrom = new ChessPosition(2, 5);
		ChessPosition mTo = new ChessPosition(2,6);

		ChessMove mv = new ChessMove(playerTwo, Game.getBoard().get(2, 5), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}

	// -----> ROOK Try's to Jump Over Other Piece <----- //
	@Order(10)
	@Test
	public void RookTryToJumpOver(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "You have a piece in your way";

		SituationMultipleChoices();

		ChessPosition mFrom = new ChessPosition(2, 5);
		ChessPosition mTo = new ChessPosition(2,7);

		ChessMove mv = new ChessMove(playerTwo, Game.getBoard().get(2, 5), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}
}
	
