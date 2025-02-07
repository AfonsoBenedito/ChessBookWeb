package domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

public class KingMovesTest {
	
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
	public void OnlyKingOnBoard(){
		
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(4);
		ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
		kingPieceB.add(0);kingPieceB.add(1);kingPieceB.add(7);kingPieceB.add(4);
		
		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(kingPieceB);
		
		this.Game.getBoard().setPieces(newBoardConfig);
	}
	
	public void KingStayInCheck() {
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(4);
		
		ArrayList<Integer> bishopPieceB = new ArrayList<Integer>();
		bishopPieceB.add(3);bishopPieceB.add(1);bishopPieceB.add(1);bishopPieceB.add(6);
		
		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(bishopPieceB);
		
		this.Game.getBoard().setPieces(newBoardConfig);
	}
	
	public void KingAndOppSideToSide() {
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(4);
		ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
		kingPieceB.add(0);kingPieceB.add(1);kingPieceB.add(7);kingPieceB.add(4);
		
		ArrayList<Integer> bishopPieceB = new ArrayList<Integer>();
		bishopPieceB.add(3);bishopPieceB.add(1);bishopPieceB.add(1);bishopPieceB.add(5);
		
		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(bishopPieceB);
		newBoardConfig.add(kingPieceB);

		this.Game.getBoard().setPieces(newBoardConfig);
	}

	public void SituationKingWithManyChoices(){
		ArrayList<ArrayList<Integer>> newBoardConfig = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> kingPieceW = new ArrayList<Integer>();
		kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);kingPieceW.add(0);

		ArrayList<Integer> kingPieceB = new ArrayList<Integer>();
		kingPieceB.add(0);kingPieceB.add(1);kingPieceB.add(5);kingPieceB.add(5);

		ArrayList<Integer> queenPieceW = new ArrayList<Integer>();
		queenPieceW.add(1);queenPieceW.add(0);queenPieceW.add(0);queenPieceW.add(1);

		ArrayList<Integer> pawnPieceW = new ArrayList<Integer>();
		pawnPieceW.add(5);pawnPieceW.add(0);pawnPieceW.add(1);pawnPieceW.add(3);

		ArrayList<Integer> bishopPieceB = new ArrayList<Integer>();
		bishopPieceB.add(3);bishopPieceB.add(1);bishopPieceB.add(5);bishopPieceB.add(6);

		newBoardConfig.add(kingPieceW);
		newBoardConfig.add(kingPieceB);
		newBoardConfig.add(queenPieceW);
		newBoardConfig.add(pawnPieceW);
		newBoardConfig.add(bishopPieceB);

		this.Game.getBoard().setPieces(newBoardConfig);
	}


	// ----------------------- TESTS ZONE ----------------------- //
	
	// -----> KING GOES TO OUT OF BOARD <----- //
	@Order(1)
	@Test
	public void KingMoveToOutOfBoard(){
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(0, 4);
				ChessPosition mTo = new ChessPosition(-1, 4);
				
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});

		assertEquals("Move out of bounds", exception.getMessage());
	}


	// -----> KING CAN'T CAPTURES OWN PIECES <----- //
	@Order(2)
	@Test
	public void KingMoveCaptureOwnPiece(){
		
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {

				int timeMilli = 0;
				ChessPosition mFrom = new ChessPosition(0, 4);
				ChessPosition mTo = new ChessPosition(1, 4);
				
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});
		assertEquals("Can't capture your own pieces", exception.getMessage());
	}


	// -----> KING CAN'T MOVE 2 SQUARES <----- //
	@Order(3)
	@Test
	public void KingMoveTwoSquares(){
		
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				OnlyKingOnBoard();

				int timeMilli = 0;
				//JOGADA DO REI
				ChessPosition mFrom = new ChessPosition(0, 4);
				ChessPosition mTo = new ChessPosition(2, 4);
				
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});
		assertEquals("Can't move more than one square", exception.getMessage());
	}


	// -----> KING STAY IN CHECK <----- //
	@Order(4)
	@Test
	public void KingMoveStayInCheck(){
		
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				KingStayInCheck();

				int timeMilli = 0;

				//JOGADA DO REI
				ChessPosition mFrom = new ChessPosition(0, 4);
				ChessPosition mTo = new ChessPosition(0,5);
				
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});
		assertEquals("KING will be in 'CHECK'", exception.getMessage());
	}


	// -----> KING STAY IN CHECK <----- //
	@Order(5)
	@Test
	public void KingMoveTheRightWay(){
			
			ChessPiece king;
			
			OnlyKingOnBoard();
			
			king = Game.getBoard().get(0, 4);
			
			int timeMilli = 0;

			//JOGADA DO REI
			ChessPosition mFrom = new ChessPosition(0, 4);
			ChessPosition mTo = new ChessPosition(1,4);
			
			ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
			
			try {
				
				Game.addMove(mv);
				
			} catch (IllegalMoveException exception) {
				
				System.out.println(exception.getMessage());
			}
			assertEquals(Game.getBoard().get(1, 4), king);
		}


	// -----> KING STAY IN SAME POSITION <----- //
	@Order(6)
	@Test
	public void KingMoveToSamePosition(){
		
		Exception exception = assertThrows(IllegalMoveException.class, new Executable() {
			
			public void execute() throws IllegalMoveException {
				OnlyKingOnBoard();

				int timeMilli = 0;

				//JOGADA DO REI
				ChessPosition mFrom = new ChessPosition(0, 4);
				ChessPosition mTo = new ChessPosition(0,4);
				
				ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
				
				Game.addMove(mv);
			}
			
		});
		assertEquals("Move out of bounds", exception.getMessage());
	}

	// -----> KING CAPTURE AN OPPONENT <----- //
	@Order(7)
	@Test
	public void KingMoveCaptureOpponent(){
		ChessPiece king;
		
		KingAndOppSideToSide();
		
		king = Game.getBoard().get(0, 4);

		int timeMilli = 0;

		//JOGADA DO REI
		ChessPosition mFrom = new ChessPosition(0, 4);
		ChessPosition mTo = new ChessPosition(1,5);
		
		ChessMove mv = new ChessMove(playerOne, Game.getBoard().get(0, 4), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			
			System.out.println(exception.getMessage());
		}
		assertEquals(Game.getBoard().get(1, 5), king);
	}

	// -----> King Try's To Castle without Rook <----- //
	@Order(8)
	@Test
	public void KingCantCastle(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "Can't Castle without a ROOK";

		SituationKingWithManyChoices();

		ChessPosition mFrom = new ChessPosition(0, 0);
		ChessPosition mTo = new ChessPosition(0,2);

		ChessMove mv = new ChessMove(playerTwo, Game.getBoard().get(0, 0), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}

	// -----> King Try's to Capture Own Piece <----- //
	@Order(9)
	@Test
	public void KingTryToEatOwn(){
		int timeMilli = 0;
		String actualMessage = null;
		String expectedMessage = "Can't capture your own pieces";

		SituationKingWithManyChoices();

		ChessPosition mFrom = new ChessPosition(0, 0);
		ChessPosition mTo = new ChessPosition(0,1);

		ChessMove mv = new ChessMove(playerTwo, Game.getBoard().get(0, 0), mFrom, mTo, timeMilli);
		try {
			Game.addMove(mv);
		} catch (IllegalMoveException exception) {
			System.out.println(exception.getMessage());
			actualMessage = exception.getMessage();
		}
		assertTrue(actualMessage.contains(expectedMessage));
	}
}