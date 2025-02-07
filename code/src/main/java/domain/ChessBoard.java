package domain;

import java.util.ArrayList;
import java.util.List;

public class ChessBoard {

	private ChessPiece[][] pieces;
	private Color turn;
	
	private ChessMove lastMove;
	
	private boolean doEnpassant = false;
	
	private ArrayList<Integer> numberPiecesCreated;
	
	private Color winner;
	private boolean finished = false;
	
	private String boardEndDescription;
	
	public ChessBoard() {
		this.turn = Color.WHITE;
		
		this.pieces = new ChessPiece[8][8];
		
		pieces[0][0] = new ChessPiece(ChessPieceKind.ROOK, Color.WHITE);
		pieces[0][1] = new ChessPiece(ChessPieceKind.KNIGHT, Color.WHITE);
		pieces[0][2] = new ChessPiece(ChessPieceKind.BISHOP, Color.WHITE);
		pieces[0][3] = new ChessPiece(ChessPieceKind.QUEEN, Color.WHITE);
		pieces[0][4] = new ChessPiece(ChessPieceKind.KING, Color.WHITE);
		pieces[0][5] = new ChessPiece(ChessPieceKind.BISHOP, Color.WHITE);
		pieces[0][6] = new ChessPiece(ChessPieceKind.KNIGHT, Color.WHITE);
		pieces[0][7] = new ChessPiece(ChessPieceKind.ROOK, Color.WHITE);
		pieces[1][0] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][1] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][2] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][3] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][4] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][5] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][6] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		pieces[1][7] = new ChessPiece(ChessPieceKind.PAWN, Color.WHITE);
		
		pieces[7][0] = new ChessPiece(ChessPieceKind.ROOK, Color.BLACK);
		pieces[7][1] = new ChessPiece(ChessPieceKind.KNIGHT, Color.BLACK);
		pieces[7][2] = new ChessPiece(ChessPieceKind.BISHOP, Color.BLACK);
		pieces[7][3] = new ChessPiece(ChessPieceKind.QUEEN, Color.BLACK);
		pieces[7][4] = new ChessPiece(ChessPieceKind.KING, Color.BLACK);
		pieces[7][5] = new ChessPiece(ChessPieceKind.BISHOP, Color.BLACK);
		pieces[7][6] = new ChessPiece(ChessPieceKind.KNIGHT, Color.BLACK);
		pieces[7][7] = new ChessPiece(ChessPieceKind.ROOK, Color.BLACK);
		pieces[6][0] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][1] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][2] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][3] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][4] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][5] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][6] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		pieces[6][7] = new ChessPiece(ChessPieceKind.PAWN, Color.BLACK);
		
		
		numberPiecesCreated = new ArrayList<Integer>();
		
		//WHITE PAWNS
		numberPiecesCreated.add(8);
		//WHITE ROOKS
		numberPiecesCreated.add(2);
		//WHITE KNIGHTS
		numberPiecesCreated.add(2);
		//WHITE BISHOPS
		numberPiecesCreated.add(2);
		//WHITE QUEENS
		numberPiecesCreated.add(1);
		
		//BLACK PAWNS
		numberPiecesCreated.add(8);
		//BLACK ROOKS
		numberPiecesCreated.add(2);
		//BLACK KNIGHTS
		numberPiecesCreated.add(2);
		//BLACK BISHOPS
		numberPiecesCreated.add(2);
		//BLACK QUEENS
		numberPiecesCreated.add(1);
		
		this.boardEndDescription = null;
	}
	
	public ChessBoard(List<ChessMove> moves) throws Exception{
		this();
		
		for (int i = 0; i < moves.size(); i++) {
			try {
				this.update(moves.get(i));
			} catch (IllegalMoveException e) {
				throw new Exception("Something went wrong, recreating the ChessBoard");
			}
		}
	}


	public boolean setPieces(ArrayList<ArrayList<Integer>> piecesAndPos) {

		this.pieces = new ChessPiece[8][8];
		
		for (ArrayList<Integer> defPiece : piecesAndPos) {
			ChessPieceKind pType = null;
			Color pColor = null;
			int pRow = defPiece.get(2);
			int pCol = defPiece.get(3);
			
			switch (defPiece.get(0)) {
				case 0:
					pType = ChessPieceKind.KING;
					break;
					
				case 1:
					pType = ChessPieceKind.QUEEN;
					break;
					
				case 2:
					pType = ChessPieceKind.ROOK;
					break;
				
				case 3:
					pType = ChessPieceKind.BISHOP;
					break;
					
				case 4:
					pType = ChessPieceKind.KNIGHT;
					break;
					
				case 5:
					pType = ChessPieceKind.PAWN;
					break;
			}
			
			switch (defPiece.get(1)) {
				case 0:
					pColor = Color.WHITE;
					break;
					
				case 1:
					pColor = Color.BLACK;
					break;
			}
			
			this.pieces[pRow][pCol] = new ChessPiece(pType, pColor);
		}
		
		return true;
	}
	
	private boolean verifyPlayerPiece(ChessPiece piece) {
		if (piece != null && this.turn == piece.getColor()) {
				return true;
			}
		
		return false;
	}
	
	private boolean verifyToPosition(ChessMove move) {
		int[] from = move.getFrom().getArrayPosition();
		int[] to = move.getTo().getArrayPosition();
		if (from[0] == to[0] && from[1] == to[1]) {
			return false;
			
		} else if (from[0] >= 0 && from[0] <= 7 && from[1] >= 0 && from[1] <= 7) {
			if (to[0] >= 0 && to[0] <= 7 && to[1] >= 0 && to[1] <= 7) {
				return true;
			}
		}
		return false;
	}
	
	private int[] findKingPosition(ChessPiece[][] pieces, Color color){
		int[] result = new int[2];
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				ChessPiece testPiece = pieces[i][j];
				if (testPiece != null) {
					if (testPiece.getColor().equals(color) && testPiece.getPieceType().equals(ChessPieceKind.KING)) {
						result[0] = i;
						result[1] = j;
						return result;
					}
				}
			}
			
		}
		return null;
	}
	
	private boolean kingInCheck(ChessPiece[][] pieces) {
		int[] kingPos = findKingPosition(pieces, this.turn);
		int i;
		int j;
		int[] pieceTestPos = new int[2];
		ChessPiece pieceTest;

		//VERTICAL CIMA
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.N);
		if (pieceTestPos[0] != -1) {
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.ROOK)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//Vertical Baixo
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.S);
		if (pieceTestPos[0] != -1) {
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.ROOK)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}

		//Horizontal direita
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.E);
		if (pieceTestPos[0] != -1) {
		
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			if (!(pieceTest.getColor().equals(this.turn))) {
				
				if ((pieceTest.getPieceType().equals(ChessPieceKind.ROOK)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//Horizontal esquerda
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.W);
		if (pieceTestPos[0] != -1) {
		
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.ROOK)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//Diagonal Cima Direita
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.NE);
		
		if (pieceTestPos[0] != -1) {
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.BISHOP)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//Diagonal Baixo Esquerda
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.SW);
		if (pieceTestPos[0] != -1) {
		
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.BISHOP)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//Diagonal Cima Esquerda
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.NW);
		if (pieceTestPos[0] != -1) {
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.BISHOP)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//Diagonal Baixo Direita
		pieceTestPos = this.nextPiece(pieces, kingPos[0], kingPos[1], Direction.SE);
		if (pieceTestPos[0] != -1) {
			pieceTest = pieces[pieceTestPos[0]][pieceTestPos[1]];
			if (!(pieceTest.getColor().equals(this.turn))) {
				if ((pieceTest.getPieceType().equals(ChessPieceKind.BISHOP)) || (pieceTest.getPieceType().equals(ChessPieceKind.QUEEN))) {
					return true;
				}
			}
		}
		
		//KNIGHTS
		i = kingPos[0];
		j = kingPos[1];
		
		if (i > 0) {
			//Verifica (i - 1)
			if (j > 0) {
				//Verifica (j - 1)
				if (j > 1) {
					//Verifica (j - 2)
					//Verifica (i - 1)(j - 2) Top
					if (pieces[i - 1][j - 2] != null) {
						if (pieces[i - 1][j - 2].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i - 1][j - 2].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
				
			}
			if (j < 7) {
				//Verifica (j + 1)
				if (j < 6) {
					//Verifica (j + 2)
					//Verifica (i - 1)(j + 2) Top
					if (pieces[i - 1][j + 2] != null) {
						if (pieces[i - 1][j + 2].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i - 1][j + 2].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
			}
			if (i > 1) {
				// Verifica (i - 2)
				if (j > 0) {
					//Verifica (j - 1)
					//Verifica (i - 2)(j - 1) Top
					if (pieces[i - 2][j - 1] != null) {
						if (pieces[i - 2][j - 1].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i - 2][j - 1].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
				if (j < 7) {
					//Verifica (j + 1)
					//Verifica (i - 2)(j + 1) Top
					if (pieces[i - 2][j + 1] != null) {
						if (pieces[i - 2][j + 1].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i - 2][j + 1].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
			}
		}
		
		
		if (i < 7) {
			//Verifica (i + 1)
			if (j > 0) {
				//Verifica (j - 1)
				if (j > 1) {
					//Verifica (j - 2)
					//Verifica (i + 1)(j - 2) Top
					if (pieces[i + 1][j - 2] != null) {
						if (pieces[i + 1][j - 2].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i + 1][j - 2].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
				
			}
			if (j < 7) {
				//Verifica (j + 1)
				if (j < 6) {
					//Verifica (j + 2)
					//Verifica (i + 1)(j + 2) Top
					if (pieces[i + 1][j + 2] != null) {
						if (pieces[i + 1][j + 2].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i + 1][j + 2].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
			}
			if (i < 6) {
				// Verifica (i + 2)
				if (j > 0) {
					//Verifica (j - 1)
					//Verifica (i + 2)(j - 1) Top
					if (pieces[i + 2][j - 1] != null) {
						if (pieces[i + 2][j - 1].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i + 2][j - 1].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
				if (j < 7) {
					//Verifica (j + 1)
					//Verifica (i + 2)(j + 1) Top
					if (pieces[i + 2][j + 1] != null) {
						if (pieces[i + 2][j + 1].getPieceType().equals(ChessPieceKind.KNIGHT) && !(pieces[i + 2][j + 1].getColor().equals(this.turn))) {
							return true;
						}
					}
				}
			}
		}
		
		//PAWNS
		if (this.turn.equals(Color.WHITE)) {
			if (i < 7) {
				if (j < 7) {
					if (pieces[i + 1][j + 1] != null) {
						if (pieces[i + 1][j + 1].getPieceType().equals(ChessPieceKind.PAWN) && pieces[i + 1][j + 1].getColor().equals(Color.BLACK)){
							return true;
						}
					}
				}
				if (j > 0) {
					if (pieces[i + 1][j - 1] != null) {
						if (pieces[i + 1][j - 1].getPieceType().equals(ChessPieceKind.PAWN) && pieces[i + 1][j - 1].getColor().equals(Color.BLACK)) {
							return true;
						}
					}
				}
			}
		}
		if (this.turn.equals(Color.BLACK)) {
			if (i > 0) {
				if (j < 7) {
					if (pieces[i - 1][j + 1] != null) {
						if (pieces[i - 1][j + 1].getPieceType().equals(ChessPieceKind.PAWN) && pieces[i - 1][j + 1].getColor().equals(Color.WHITE)) {
							return true;
						}
					}
				}
				if (j > 0) {
					if (pieces[i - 1][j - 1] != null) {
						if (pieces[i - 1][j - 1].getPieceType().equals(ChessPieceKind.PAWN) && pieces[i - 1][j - 1].getColor().equals(Color.WHITE)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private int[] nextPiece(ChessPiece[][] pieces, int row, int col, Direction direction){
		int i, j;
		
		int[] result = {-1,-1};
		// N, NE, E (->), SE, S, SW, (<-) W, NW
		switch (direction) {
			case N:
				// Vertical (cima)
				for (i = row + 1, j = col; i <= 7; i++) {
					
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}	
				break;
				
			case S:
				// Vertical (baixo)
				for (i = row - 1, j = col; i >= 0; i--) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}	
				break;
				
			case E:
				// Horizontal (direita)
				for (i = row , j = col + 1 ; j <= 7; j++) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}	
				break;
				
			case W:
				// Horizontal (esquerda)
				for (i = row , j = col - 1 ; j >= 0; j--) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}
				break;
				
			case NE:
				// Diagonal (cima/direita)
				for (i = row + 1 , j = col + 1 ; i <= 7 && j <= 7; i++, j++) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}
				break;
				
			case SW:
				// Diagonal (baixo/esquerda)
				for (i = row - 1 , j = col - 1 ; i >= 0 && j >= 0; i--, j--) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}
				break;
				
			case NW:
				// Diagonal (cima/esquerda)
				for (i = row + 1 , j = col - 1 ; i <= 7 && j >= 0; i++, j--) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}
				break;
				
			case SE:
				// Diagonal (baixo/direita)
				for (i = row - 1 , j = col + 1 ; i >= 0 && j <= 7; i--, j++) {
					if (pieces[i][j] != null) {
						result[0] = i;
						result[1] = j;
						break;
					}
				}
				break;
		}
		return result;
	}
	
	
	public void update(ChessMove move) throws IllegalMoveException {
		if (this.finished) {
			if (this.winner != null) {
				if (this.winner.equals(Color.WHITE)) {
					throw new IllegalMoveException("Game is already over, White won");
				} else if (this.winner.equals(Color.BLACK)){
					throw new IllegalMoveException("Game is already over, Black won");
				}
			} else {
				throw new IllegalMoveException("Game is already over, Stalemate");
			}
		}
		
		boolean testResult = false;
		
		//INTEGRIDADE DO MOVE (validar se a pe�a no From � do jogador atual e verificar se os valores est�o entre 0 e 7 inclusiv�s)
		if (this.verifyPlayerPiece(move.getPiece())) {
			if (this.verifyToPosition(move)){
				switch(move.getPiece().getPieceType()) {
					case ROOK:
						testResult = this.testRookMove(move);
						break;

					case BISHOP:
						testResult = this.testBishopMove(move);
						break;

					case QUEEN:
						testResult = this.testQueenMove(move);
						break;

					case KING:
						testResult = this.testKingMove(move);
						break;

					case PAWN:
						testResult = this.testPawnMove(move);
						break;

					case KNIGHT:
						testResult = this.testKnightMove(move);
						break;
				}
			} else {
				throw new IllegalMoveException("Move out of bounds");
			}
			
		} else {
			throw new IllegalMoveException("You don't have access to that square");
		}

		if (testResult) {
			
			this.applyMove(move);
			this.lastMove = move;
			
			if (this.turn.equals(Color.WHITE)){
				this.turn = Color.BLACK;
			} else {
				this.turn = Color.WHITE;
			}
			
		} else {
			throw new IllegalMoveException("KING will be in 'CHECK'");
		}
		
		if (this.noMoves(this.pieces)) {
			this.finished = true;
			
			if (this.kingInCheck(this.pieces)) {
				if (this.turn.equals(Color.WHITE)) {
					this.winner = Color.BLACK;
				} else {
					this.winner = Color.WHITE;
				}
				
				this.boardEndDescription = "Checkmate";
			} else {
				this.boardEndDescription = "There was a Stalemate";
			}
			
		}
	}
	
	
	private boolean testRookMove(ChessMove move) throws IllegalMoveException {
		Direction moveDir = findMoveDirection(move);
		
		if (!(moveDir == Direction.N || moveDir == Direction.S || moveDir == Direction.E || moveDir == Direction.W)) {
			
			throw new IllegalMoveException("ROOK doesn't move that way");
			
		} else {
			
			int[] from = move.getFrom().getArrayPosition();
			
			int[] to = move.getTo().getArrayPosition();
			
			int[] nextPiece = this.nextPiece(this.pieces, from[0],from[1],moveDir);
			
			if (nextPiece[0] == -1 && nextPiece[1] == -1) {
				return this.testApplyMove(move);
			}
			
			switch(moveDir) {
				case N:
					if (to[0] < nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] > nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DAS PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
					
				case S:
					if (to[0] > nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] < nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DAS PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;

				case E:
					if (to[1] < nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] > nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DAS PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
					
				case W:
					if (to[1] > nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] < nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DAS PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
			}
		}

		return false;
	}
	
	private boolean testBishopMove(ChessMove move) throws IllegalMoveException {
		Direction moveDir = findMoveDirection(move);
		
		if (!(moveDir == Direction.NE || moveDir == Direction.SW || moveDir == Direction.NW || moveDir == Direction.SE)) {
			throw new IllegalMoveException("BISHOP doesn't move that way");
		} else {
			
			int[] from = move.getFrom().getArrayPosition();
			int[] to = move.getTo().getArrayPosition();
			int[] nextPiece = this.nextPiece(this.pieces,from[0],from[1],moveDir);
			if (nextPiece[0] == -1 && nextPiece[1] == -1) {
				return this.testApplyMove(move);
			}
			switch(moveDir) {
				case NE:
					if (to[0] < nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] > nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
					
				case SW:
					if (to[0] > nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] < nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
					
				case NW:
					if (to[1] > nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] < nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
					
				case SE:
					if (to[1] < nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] > nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
			}
		}
		return false;
		
	}
	
	private boolean testQueenMove(ChessMove move) throws IllegalMoveException {
		
		Direction moveDir = findMoveDirection(move);
		
		if (moveDir == null) {
			
			throw new IllegalMoveException("QUEEN doesn't move that way");
			
		} else {
			
			int[] from = move.getFrom().getArrayPosition();
			int[] to = move.getTo().getArrayPosition();
			int[] nextPiece = this.nextPiece(this.pieces, from[0],from[1],moveDir);
			
			if (nextPiece[0] == -1 && nextPiece[1] == -1) {
				
				return this.testApplyMove(move);
			}
			
			switch(moveDir) {
			
				case N:
					
					if (to[0] < nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] > nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
					
				case S:
					
					if (to[0] > nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] < nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
					
				case E:
					if (to[1] < nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] > nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
					
				case W:
					
					if (to[1] > nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
					} else if (to[1] < nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
					
				case NE:
					
					if (to[0] < nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] > nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					break;
					
				case SW:
					
					if (to[0] > nextPiece[0]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[0] < nextPiece[0]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[0] == nextPiece[0]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[0] == nextPiece[0]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
					
				case NW:
					
					if (to[1] > nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] < nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
					
				case SE:
					
					if (to[1] < nextPiece[1]) {
						//DESTINO ANTES DA PROXIMA PECA
						return this.testApplyMove(move);
						
					} else if (to[1] > nextPiece[1]){
						//NAO PODE SALTAR PECAS
						throw new IllegalMoveException("You have a piece in your way");
						
					} else if ((to[1] == nextPiece[1]) && (this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
						throw new IllegalMoveException("Can't capture your own pieces");
						
					} else if ((to[1] == nextPiece[1]) && !(this.pieces[nextPiece[0]][nextPiece[1]].getColor().equals(this.turn))){
						//ESTA A TENTAR COMER
						return this.testApplyMove(move);
					}
					
					break;
			}
		}
		return false;
		
	}
	
	private boolean testKingMove(ChessMove move) throws IllegalMoveException {
		
		Direction moveDir = findMoveDirection(move);
		
		if (moveDir == null) {
			
			throw new IllegalMoveException("KING doesn't move that way");
			
		} else {
			int[] from = move.getFrom().getArrayPosition();
			int[] to = move.getTo().getArrayPosition();
			
			switch(moveDir) {
			
				case N:
					
					if (to[0] - from[0] == 1) {
						
						if (this.pieces[to[0]][to[1]] != null) {
							
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
							
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
					} else {
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case S:
					
					if (to[0] - from[0] == -1) {
						
						if (this.pieces[to[0]][to[1]] != null) {
						
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						
						}
					} else {
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case E:
					
					if (to[1] - from[1] == 1) {
						
						if (this.pieces[to[0]][to[1]] != null) {
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						} else {
							
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
					} else {
						if (to[1] - from[1] == 2) {
							//testCastleE
							return this.testCastle(move, moveDir);
						}
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case W:
					
					if (to[1] - from[1] == -1) {
						if (this.pieces[to[0]][to[1]] != null) {
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
							
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
					} else {
						if (to[1] - from[1] == -2) {
							//testCastleE
							return this.testCastle(move, moveDir);
						}
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case NE:
					
					if (to[0] - from[0] == 1) {
						if (this.pieces[to[0]][to[1]] != null) {
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
					} else {
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case SW:
					
					if (to[0] - from[0] == -1) {
						if (this.pieces[to[0]][to[1]] != null) {
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
					} else {
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case NW:
					
					if (to[0] - from[0] == 1) {
						
						if (this.pieces[to[0]][to[1]] != null) {
						
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
					} else {
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
					
				case SE:
					
					if (to[0] - from[0] == -1) {
						
						if (this.pieces[to[0]][to[1]] != null) {
						
							if (this.pieces[to[0]][to[1]].getColor().equals(this.turn)) {
								//NAO PODE IR PARA CIMA DE PECAS DA MESMA COR
								throw new IllegalMoveException("Can't capture your own pieces");
								
							} else {
								//ESTA A TENTAR COMER
								return this.testApplyMove(move);
							}
						} else {
							//ANDA NORMAL
							return this.testApplyMove(move);
						}
						
						
					} else {
						//SO PODE ANDAR 1 CASA
						throw new IllegalMoveException("Can't move more than one square");
					}
			}
			
		}
		return false;
	}
	
	private boolean testPawnMove(ChessMove move) throws IllegalMoveException {
		
		doEnpassant = false;
		
		Direction moveDir = findMoveDirection(move);
		
		if (this.turn.equals(Color.WHITE)) {
			
			if (!(moveDir == Direction.N || moveDir == Direction.NW || moveDir == Direction.NE)) {
				
				throw new IllegalMoveException("PAWN doesn't move that way");
				
			} else {
				
				int[] from = move.getFrom().getArrayPosition();
				int[] to = move.getTo().getArrayPosition();
				
				switch(moveDir) {
				
					case N:
						
						if (to[0] - from[0] == 1) {
							
							if (this.pieces[to[0]][to[1]] == null) {
								//ESTA A TENTAR ANDAR 1 PARA FRENTE
								return this.testApplyMove(move);
								
							} else {
								//TEM UMA PECA A BLOQUEAR
								throw new IllegalMoveException("You have a piece in your way");
							}
							
						} else if (to[0] - from[0] == 2) {
							
							if (from[0] == 1) {
								if (this.pieces[to[0] - 1][to[1]] == null && this.pieces[to[0]][to[1]] == null) {
									//ESTA A TENTAR ANDAR 1 PARA FRENTE
									return this.testApplyMove(move);
									
								} else {
									//TEM UMA PECA A BLOQUEAR
									throw new IllegalMoveException("You have a piece in your way");
								}
								
							} else {
								//NAO PODE ANDAR 2 PARA FRENTE POIS JA ANDOU ANTERIORMENTE
								throw new IllegalMoveException("PAWN can't move that way");
							}
							
						} else {
							//NAO PODE ANDAR MAIS DE 2 CASAS
							throw new IllegalMoveException("PAWN can't move that way");
						}
						
					case NW:
						
						if (to[0] - from[0] == 1) {
							
							if (this.pieces[to[0]][to[1]] != null) {
								if (!(this.pieces[to[0]][to[1]].getColor().equals(this.turn))) {
									//ESTA A TENTAR COMER
									return this.testApplyMove(move);
									
								} else {
									//NAO PODE COMER UMA PECA PROPRIA
									throw new IllegalMoveException("Can't capture your own pieces");
								}
								
							} else {
								if (this.pieces[to[0] - 1][to[1]] != null) {
									if (this.pieces[to[0] - 1][to[1]].getChessPieceKind().equals(ChessPieceKind.PAWN) && this.pieces[to[0] - 1][to[1]].getColor().equals(Color.BLACK)) {
										if (this.lastMove.getTo().getRow() == to[0] - 1  && Math.abs(this.lastMove.getFrom().getRow() - this.lastMove.getTo().getRow()) == 2) {
											if (this.testApplyMove(move)){
												doEnpassant = true;
												return true;
											}
										}
									}
									throw new IllegalMoveException("Can't EnPassant");
								} else {
									//NAO PODE COMER UM QUADRADO VAZIO NADA
									throw new IllegalMoveException("Can't capture an empty square");
								}
								
								
								
							}
							
							
							
						} else {
							//NAO PODE TENTAR COMER MAIS DE UMA PECA // 2 NA DIAGONAL
							throw new IllegalMoveException("Can't capture more than one piece");
						}
						
					case NE:
						
						if (to[0] - from[0] == 1) {
							
							if (this.pieces[to[0]][to[1]] != null) {
								if (!(this.pieces[to[0]][to[1]].getColor().equals(this.turn))) {
									//ESTA A TENTAR COMER
									return this.testApplyMove(move);
									
								} else {
									//NAO PODE COMER UMA PECA PROPRIA
									throw new IllegalMoveException("Can't capture your own pieces");
								}
							} else {
								if (this.pieces[to[0] - 1][to[1]] != null) {
									if (this.pieces[to[0] - 1][to[1]].getChessPieceKind().equals(ChessPieceKind.PAWN) && this.pieces[to[0] - 1][to[1]].getColor().equals(Color.BLACK)) {
										if (this.lastMove.getTo().getRow() == to[0] - 1  && Math.abs(this.lastMove.getFrom().getRow() - this.lastMove.getTo().getRow()) == 2) {
											if (this.testApplyMove(move)){
												doEnpassant = true;
												return true;
											}
										}
									}
									throw new IllegalMoveException("Can't EnPassant");
								} else {
									//NAO PODE COMER UM QUADRADO VAZIO NADA
									throw new IllegalMoveException("Can't capture an empty square");
								}
							}
							
							
						} else {
							//NAO PODE TENTAR COMER MAIS DE UMA PECA // 2 NA DIAGONAL
							throw new IllegalMoveException("Can't capture more than one piece");
						}
				}
			}
			
		} else if (this.turn.equals(Color.BLACK)) {
			
			if (!(moveDir == Direction.S || moveDir == Direction.SW || moveDir == Direction.SE)) {
				
				return false;
				
			} else {
				
				int[] from = move.getFrom().getArrayPosition();
				int[] to = move.getTo().getArrayPosition();
				
				switch(moveDir) {
				
					case S:
						
						if (to[0] - from[0] == -1) {
							
							if (this.pieces[to[0]][to[1]] == null) {
							
								//ESTA A TENTAR ANDAR 1 PARA FRENTE
								return this.testApplyMove(move);
								
							} else {
								//TEM UMA PECA A BLOQUEAR
								throw new IllegalMoveException("You have a piece in your way");
							}
							
						} else if (to[0] - from[0] == -2) {
							
							if (from[0] == 6) {
								
								if (this.pieces[to[0] + 1][to[1]] == null && this.pieces[to[0]][to[1]] == null) {
									//ESTA A TENTAR ANDAR 2 PARA FRENTE
									return this.testApplyMove(move);
									
								} else {
									//TEM UMA PECA A BLOQUEAR
									throw new IllegalMoveException("You have a piece in your way");
								}
								
							} else {
								throw new IllegalMoveException("PAWN can't move that way");
							}
							
						} else {
							throw new IllegalMoveException("PAWN can't move that way");
						}
						
					case SW:
						
						if (to[0] - from[0] == -1) {
							
							if (this.pieces[to[0]][to[1]] != null) {
								if (!(this.pieces[to[0]][to[1]].getColor().equals(this.turn))) {
									//Est� a tentar comer
									return this.testApplyMove(move);
									
								} else {
									//NAO PODE COMER UMA PECA PROPRIA
									throw new IllegalMoveException("Can't capture your own pieces");
								} 
							} else {
								if (this.pieces[to[0] + 1][to[1]] != null) {
									if (this.pieces[to[0] + 1][to[1]].getChessPieceKind().equals(ChessPieceKind.PAWN) && this.pieces[to[0] + 1][to[1]].getColor().equals(Color.WHITE)) {
										if (this.lastMove.getTo().getRow() == to[0] + 1 && Math.abs(this.lastMove.getFrom().getRow() - this.lastMove.getTo().getRow()) == 2) {
											if (this.testApplyMove(move)){
												doEnpassant = true;
												return true;
											}
										}
									}
									throw new IllegalMoveException("Can't EnPassant");
								} else {
									//NAO PODE COMER UM QUADRADO VAZIO NADA
									throw new IllegalMoveException("Can't capture an empty square");
								}
							}
							
							
							
						} else {
							//N�o pode tentar comer mais de 1 casa
							throw new IllegalMoveException("Can't capture more than one piece");
						}
						
					case SE:
						
						if (to[0] - from[0] == -1) {
							
							if (this.pieces[to[0]][to[1]] != null) {
								if (!(this.pieces[to[0]][to[1]].getColor().equals(this.turn))) {
									//Est� a tentar comer
									return this.testApplyMove(move);
									
								} else {
									//N�o pode comer uma pe�a propria
									throw new IllegalMoveException("Can't capture your own pieces");
								}
							} else {
								if (this.pieces[to[0] + 1][to[1]] != null) {
									if (this.pieces[to[0] + 1][to[1]].getChessPieceKind().equals(ChessPieceKind.PAWN) && this.pieces[to[0] + 1][to[1]].getColor().equals(Color.WHITE)) {
										if (this.lastMove.getTo().getRow() == to[0] + 1  && Math.abs(this.lastMove.getFrom().getRow() - this.lastMove.getTo().getRow()) == 2) {
											if (this.testApplyMove(move)){
												doEnpassant = true;
												return true;
											}
										}
									}
									throw new IllegalMoveException("Can't EnPassant");
								} else {
									//NAO PODE COMER UM QUADRADO VAZIO NADA
									throw new IllegalMoveException("Can't capture an empty square");
								}
							}
							
							
						} else {
							//N�o pode tentar comer mais de 1 casa
							throw new IllegalMoveException("Can't capture more than one piece");
						}
				}
			}
		}
		return false;
	}
	
	private boolean testKnightMove(ChessMove move) throws IllegalMoveException {
		
		Direction moveDir = findMoveDirection(move);
		
		if (moveDir != null) {
			//CAVALOS NAO TEM DIRECAO
			throw new IllegalMoveException("KNIGHT doesn't move that way");
			
		} else {
			
			int[] from = move.getFrom().getArrayPosition();
			int[] to = move.getTo().getArrayPosition();
			
			if (to[0] - from[0] == 1 || to[0] - from[0] == -1) {
				
				if (to[1] - from[1] == 2 || to[1] - from[1] == -2) {
					
					if (this.pieces[to[0]][to[1]] != null) {
						
						if (!(this.pieces[to[0]][to[1]].getColor().equals(this.turn))) {
							//CAVALO SUBIU/DESCEU 1 LINHA
							return this.testApplyMove(move);
							
						} else {
							//NAO PODE COMER PECAS DA MESMA COR
							throw new IllegalMoveException("Can't capture your own pieces");
						}
						
					} else {
						//CAVALO SUBIU/DESCEU 1 LINHA
						return this.testApplyMove(move);
					}
					
				} else {
					//MOVIMENTO ERRADO
					throw new IllegalMoveException("KNIGHT doesn't move that way");
				}
				
			} else if (to[0] - from[0] == 2 || to[0] - from[0] == -2) {
				
				if (to[1] - from[1] == 1 || to[1] - from[1] == -1) {
					
					if (this.pieces[to[0]][to[1]] != null) {
						
						if (!(this.pieces[to[0]][to[1]].getColor().equals(this.turn))) {
							//CAVALO SUBIU/DESCEU 1 LINHA
							return this.testApplyMove(move);
							
						} else {
							//NAO PODE COMER PECAS DA MESMA COR
							throw new IllegalMoveException("Can't capture your own pieces");
						}
						
					} else {
						//CAVALO SUBIU/DESCEU 1 LINHA
						return this.testApplyMove(move);
					}
				} else {
					//MOVIMENTO ERRADO
					throw new IllegalMoveException("KNIGHT doesn't move that way");
				}
			}
		}
		return false;
		
	}
	
	
	private boolean testCastle(ChessMove move, Direction moveDir) throws IllegalMoveException {

		int timeMilli = 0;

		if (move.getPiece().getColor().equals(Color.WHITE)) {
			
			if (moveDir.equals(Direction.E)) {
				if (move.getPiece().getNumberOfMoves() == 0) {
					if (!this.kingInCheck(this.pieces)) {
						if (this.pieces[0][7] != null) {
							
							if (this.pieces[0][7].getChessPieceKind().equals(ChessPieceKind.ROOK)) {
								if (this.pieces[0][7].getColor().equals(Color.WHITE)) {
									if (this.pieces[0][7].getNumberOfMoves() == 0) {
										if (this.nextPiece(this.pieces, 0, 4, Direction.E)[1] == 7) {
											
											ChessPosition testPos1 = new ChessPosition(0,5);
											ChessPosition testPos2 = new ChessPosition(0,6);
											ChessMove testMove1 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos1, timeMilli);
											ChessMove testMove2 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos2, timeMilli);
											
											if (this.testApplyMove(testMove1) && this.testApplyMove(testMove2)) {
												return true;
											} else {
												throw new IllegalMoveException("Can't Castle, KING will pass a CHECK square or will be in CHECK");
											}
										
											
										} else {
											throw new IllegalMoveException("There is a piece in the way");
										}	
									} else {
										throw new IllegalMoveException("Can't Castle if ROOK already moved");
									}
								} else {
									throw new IllegalMoveException("Can't Castle with a ROOK of a different color");
								}
							} else {
								throw new IllegalMoveException("Can't Castle without a ROOK");
							}
						} else {
							throw new IllegalMoveException("Can't Castle without a ROOK");
						}
					} else {
						throw new IllegalMoveException("Can't Castle if KING in CHECK");
					}
				} else {
					throw new IllegalMoveException("Can't Castle if KING already moved");
				}
			} else if (moveDir.equals(Direction.W)) {
				
				if (move.getPiece().getNumberOfMoves() == 0) {
					if (!this.kingInCheck(this.pieces)) {
						if (this.pieces[0][0] != null) {
							
							if (this.pieces[0][0].getChessPieceKind().equals(ChessPieceKind.ROOK)) {
								if (this.pieces[0][0].getColor().equals(Color.WHITE)) {
									if (this.pieces[0][0].getNumberOfMoves() == 0) {
										if (this.nextPiece(this.pieces, 0, 4, Direction.W)[1] == 0) {
											
											ChessPosition testPos1 = new ChessPosition(0,2);
											ChessPosition testPos2 = new ChessPosition(0,3);
											ChessMove testMove1 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos1, timeMilli);
											ChessMove testMove2 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos2, timeMilli);
											
											if (this.testApplyMove(testMove1) && this.testApplyMove(testMove2)) {
												return true;
											} else {
												throw new IllegalMoveException("Can't Castle, KING will pass a CHECK square or will be in CHECK");
											}
										
											
										} else {
											throw new IllegalMoveException("There is a piece in the way");
										}	
									} else {
										throw new IllegalMoveException("Can't Castle if ROOK already moved");
									}
								} else {
									throw new IllegalMoveException("Can't Castle with a ROOK of a different color");
								}
							} else {
								throw new IllegalMoveException("Can't Castle without a ROOK");
							}
						} else {
							throw new IllegalMoveException("Can't Castle without a ROOK");
						}
					} else {
						throw new IllegalMoveException("Can't Castle if KING in CHECK");
					}
				} else {
					throw new IllegalMoveException("Can't Castle if KING already moved");
				}
				
			}
			
		} else if (move.getPiece().getColor().equals(Color.BLACK)) {
			
			if (moveDir.equals(Direction.E)) {
				if (move.getPiece().getNumberOfMoves() == 0) {
					if (!this.kingInCheck(this.pieces)) {
						if (this.pieces[7][7] != null) {
							
							if (this.pieces[7][7].getChessPieceKind().equals(ChessPieceKind.ROOK)) {
								if (this.pieces[7][7].getColor().equals(Color.BLACK)) {
									if (this.pieces[7][7].getNumberOfMoves() == 0) {
										if (this.nextPiece(this.pieces, 7, 4, Direction.E)[1] == 7) {
											
											ChessPosition testPos1 = new ChessPosition(7,5);
											ChessPosition testPos2 = new ChessPosition(7,6);
											ChessMove testMove1 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos1, timeMilli);
											ChessMove testMove2 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos2, timeMilli);
											
											if (this.testApplyMove(testMove1) && this.testApplyMove(testMove2)) {
												return true;
											} else {
												throw new IllegalMoveException("Can't Castle, KING will pass a CHECK square or will be in CHECK");
											}
										
											
										} else {
											throw new IllegalMoveException("There is a piece in the way");
										}	
									} else {
										throw new IllegalMoveException("Can't Castle if ROOK already moved");
									}
								} else {
									throw new IllegalMoveException("Can't Castle with a ROOK of a different color");
								}
							} else {
								throw new IllegalMoveException("Can't Castle without a ROOK");
							}
						} else {
							throw new IllegalMoveException("Can't Castle without a ROOK");
						}
					} else {
						throw new IllegalMoveException("Can't Castle if KING in CHECK");
					}
				} else {
					throw new IllegalMoveException("Can't Castle if KING already moved");
				}
			} else if (moveDir.equals(Direction.W)) {
				
				if (move.getPiece().getNumberOfMoves() == 0) {
					if (!this.kingInCheck(this.pieces)) {
						if (this.pieces[7][0] != null) {
							
							if (this.pieces[7][0].getChessPieceKind().equals(ChessPieceKind.ROOK)) {
								if (this.pieces[7][0].getColor().equals(Color.BLACK)) {
									if (this.pieces[7][0].getNumberOfMoves() == 0) {
										if (this.nextPiece(this.pieces, 7, 4, Direction.W)[1] == 0) {
											
											ChessPosition testPos1 = new ChessPosition(7,2);
											ChessPosition testPos2 = new ChessPosition(7,3);
											ChessMove testMove1 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos1, timeMilli);
											ChessMove testMove2 = new ChessMove(move.getPlayer(), move.getPiece(), move.getFrom(), testPos2, timeMilli);
											
											if (this.testApplyMove(testMove1) && this.testApplyMove(testMove2)) {
												return true;
											} else {
												throw new IllegalMoveException("Can't Castle, KING will pass a CHECK square or will be in CHECK");
											}
										
											
										} else {
											throw new IllegalMoveException("There is a piece in the way");
										}	
									} else {
										throw new IllegalMoveException("Can't Castle if ROOK already moved");
									}
								} else {
									throw new IllegalMoveException("Can't Castle with a ROOK of a different color");
								}
							} else {
								throw new IllegalMoveException("Can't Castle without a ROOK");
							}
						} else {
							throw new IllegalMoveException("Can't Castle without a ROOK");
						}
					} else {
						throw new IllegalMoveException("Can't Castle if KING in CHECK");
					}
				} else {
					throw new IllegalMoveException("Can't Castle if KING already moved");
				}
				
			}
			
		}
		
		
		return false;
	}
	
	private Direction findMoveDirection(ChessMove move) throws IllegalMoveException {
		
		int[] from = move.getFrom().getArrayPosition();
		int[] to = move.getTo().getArrayPosition();
		
		if (from[0] == to[0]) {
			
			if (from[1] == to[1]) {
				// EXCECAO (NAO PODE FICAR NO MESMO SITIO)
				throw new IllegalMoveException("Can't stay in the same spot");
				
			} else if (from[1] < to[1]) {
				return Direction.E;
				
			} else if (from[1] > to[1]) {
				return Direction.W;
			}
			
		} else if (from[0] < to[0]) {
			
			if (from[1] == to[1]) {
				return Direction.N;
				
			} else if (((double)(to[1] - from[1])/(double)(to[0] - from[0])) == 1) {
				return Direction.NE;
				
			} else if (((double)(to[1] - from[1])/(double)(to[0] - from[0])) == -1) {
				return Direction.NW;
				
			} else {
				return null;
			}
			
		} else if (from[0] > to[0]) {
			
			if (from[1] == to[1]) {
				return Direction.S;
				
			} else if (((double)(to[1] - from[1])/(double)(to[0] - from[0])) == 1) {
				return Direction.SW;
				
			} else if (((double)(to[1] - from[1])/(double)(to[0] - from[0])) == -1) {
				return Direction.SE;
				
			} else {
				return null;
			}
		}
		return null;	
	}
	
	private boolean testApplyMove(ChessMove move) {
		
		ChessPiece[][] testBoard = new ChessPiece[8][8];
		
		for(int i = 0; i <= 7; i++) {
			
			for(int j = 0; j <= 7; j++) {
				
				testBoard[i][j] = this.pieces[i][j];	
			}
		}
		
		int[] from = move.getFrom().getArrayPosition();
		int[] to = move.getTo().getArrayPosition();
		
		if (this.turn.equals(Color.WHITE)) {
			//PROMOCAO BRANCO
			
			if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && to[0] == 7) {
				
				ChessPiece newPiece = new ChessPiece(move.getPromotion(), this.turn);
				
				testBoard[to[0]][to[1]] = newPiece;
				testBoard[from[0]][from[1]] = null;
				
			} else if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && this.doEnpassant) {
				
				testBoard[to[0]][to[1]] = testBoard[from[0]][from[1]];
				testBoard[from[0]][from[1]] = null;
				
				testBoard[to[0] - 1][to[1]] = null;
				
			} else {
				testBoard[to[0]][to[1]] = testBoard[from[0]][from[1]];
				testBoard[from[0]][from[1]] = null;
			}
			
		} else if (this.turn.equals(Color.BLACK)) {
			//PROMOCAO PRETO
			
			if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && to[0] == 0) {
				
				ChessPiece newPiece = new ChessPiece(move.getPromotion(), this.turn);
				
				testBoard[to[0]][to[1]] = newPiece;
				testBoard[from[0]][from[1]] = null;
				
			} else if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && this.doEnpassant) {
				
				testBoard[to[0]][to[1]] = testBoard[from[0]][from[1]];
				testBoard[from[0]][from[1]] = null;
				
				testBoard[to[0] + 1][to[1]] = null;
				
			} else {
				testBoard[to[0]][to[1]] = testBoard[from[0]][from[1]];
				testBoard[from[0]][from[1]] = null;
			}
		}
		
		return !(this.kingInCheck(testBoard));
	}
	
	private void applyMove(ChessMove move) throws IllegalMoveException {
		
		int[] from = move.getFrom().getArrayPosition();
		int[] to = move.getTo().getArrayPosition();
		
		ChessPiece toPiece = this.pieces[to[0]][to[1]];
		
		if (this.turn.equals(Color.WHITE)) {
			//PROMOCAO BRANCO
			
			if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && to[0] == 7) {
				
				ChessPiece newPiece = new ChessPiece(move.getPromotion(), this.turn);
				
				this.pieces[to[0]][to[1]] = newPiece;
				this.pieces[from[0]][from[1]] = null;
				
				switch(newPiece.getChessPieceKind()) {
					case PAWN:
						break;
					case ROOK:
						this.numberPiecesCreated.set(1, this.numberPiecesCreated.get(1) + 1);
						break;
					case KNIGHT:
						this.numberPiecesCreated.set(2, this.numberPiecesCreated.get(2) + 1);
						break;
					case BISHOP:
						this.numberPiecesCreated.set(3, this.numberPiecesCreated.get(3) + 1);
						break;
					case QUEEN:
						this.numberPiecesCreated.set(4, this.numberPiecesCreated.get(4) + 1);
						break;
					case KING:
						break;
				}
				
			} else if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && this.doEnpassant) {
				
				this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
				this.pieces[from[0]][from[1]] = null;
				
				this.pieces[to[0] - 1][to[1]] = null;
				
				this.doEnpassant = false;
				
			} else if(move.getPiece().getChessPieceKind() == ChessPieceKind.KING && Math.abs(to[1] - from[1]) == 2) {
				try {
					if (this.findMoveDirection(move) == Direction.E) {
						this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
						this.pieces[from[0]][from[1]] = null;
						
						this.pieces[0][5] = this.pieces[0][7];
						this.pieces[0][7] = null;
					} else if (this.findMoveDirection(move) == Direction.W) {
						this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
						this.pieces[from[0]][from[1]] = null;
						
						this.pieces[0][3] = this.pieces[0][0];
						this.pieces[0][0] = null;
					}
				} catch (Exception e) {
					throw new IllegalMoveException("Something went wrong");
				}
			} else {
				this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
				this.pieces[from[0]][from[1]] = null;
			}
			
		} else if (this.turn.equals(Color.BLACK)) {
			//PROMOCAO PRETO
			
			if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && to[0] == 0) {
				
				ChessPiece newPiece = new ChessPiece(move.getPromotion(), this.turn);
				
				this.pieces[to[0]][to[1]] = newPiece;
				this.pieces[from[0]][from[1]] = null;
				
				switch(newPiece.getChessPieceKind()) {
					case PAWN:
						break;
					case ROOK:
						this.numberPiecesCreated.set(6, this.numberPiecesCreated.get(6) + 1);
						break;
					case KNIGHT:
						this.numberPiecesCreated.set(7, this.numberPiecesCreated.get(7) + 1);
						break;
					case BISHOP:
						this.numberPiecesCreated.set(8, this.numberPiecesCreated.get(8) + 1);
						break;
					case QUEEN:
						this.numberPiecesCreated.set(9, this.numberPiecesCreated.get(9) + 1);
						break;
					case KING:
						break;
				}
				
			} else if (move.getPiece().getPieceType() == ChessPieceKind.PAWN && this.doEnpassant) {
				
				this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
				this.pieces[from[0]][from[1]] = null;
				
				this.pieces[to[0] + 1][to[1]] = null;
				
				this.doEnpassant = false;
				
			} else if(move.getPiece().getChessPieceKind() == ChessPieceKind.KING && Math.abs(to[1] - from[1]) == 2) {
				
				try {
					if (this.findMoveDirection(move) == Direction.E) {
						this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
						this.pieces[from[0]][from[1]] = null;
						
						this.pieces[7][5] = this.pieces[7][7];
						this.pieces[7][7] = null;
					} else if (this.findMoveDirection(move) == Direction.W) {
						this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
						this.pieces[from[0]][from[1]] = null;
						
						this.pieces[7][3] = this.pieces[7][0];
						this.pieces[7][0] = null;
					}
				} catch (Exception e) {
					throw new IllegalMoveException("Something went wrong");
				}
				
			} else {
				this.pieces[to[0]][to[1]] = this.pieces[from[0]][from[1]];
				this.pieces[from[0]][from[1]] = null;
			}
		}
		
		this.pieces[to[0]][to[1]].setNumberOfMoves(this.pieces[to[0]][to[1]].getNumberOfMoves() + 1);
	}
	
	public ChessPiece get(int row, int col) {
		
		return this.pieces[row][col];
	}
	
	private boolean noMoves(ChessPiece[][] pieces) {
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (pieces[i][j] != null) {
					if (pieces[i][j].getColor() == this.turn) {
						int[] pos = {i,j};
						ChessPieceKind pieceKind = pieces[i][j].getChessPieceKind();
						switch(pieceKind) {
							case KING:
								if (!(testPieceMove(testAllKingMoves(pieces,pos), ChessPieceKind.KING))) {
									return false;
								} else {
									break;
								}
							case ROOK:
								if (!(testPieceMove(testAllRookMoves(pieces,pos), ChessPieceKind.ROOK))) {
									return false;
								} else {
									break;
								}
							case BISHOP:
								if (!(testPieceMove(testAllBishopMoves(pieces,pos), ChessPieceKind.BISHOP))) {
									return false;
								} else {
									break;
								}
							case QUEEN:
								if (!(testPieceMove(testAllQueenMoves(pieces,pos), ChessPieceKind.QUEEN))) {
									return false;
								} else {
									break;
								}
							case KNIGHT:
								if (!(testPieceMove(testAllKnightMoves(pieces,pos), ChessPieceKind.KNIGHT))) {
									return false;
								} else {
									break;
								}
							case PAWN:
								if (!(testPieceMove(testAllPawnMoves(pieces,pos), ChessPieceKind.PAWN))) {
									return false;
								} else {
									break;
								}
						}
						
					}
				}
				
			}
		}
		
		
		return true;
	}
	
	private List<ChessMove> testAllKingMoves(ChessPiece[][] pieces, int[] pos) {
		
		ArrayList<ChessMove> testMoves = new ArrayList<ChessMove>();
		
		ChessPlayer testPlayer = new ChessPlayer("test","test@none");
		
		ChessPiece testPiece = pieces[pos[0]][pos[1]];
		
		ChessPosition from = new ChessPosition(pos[0],pos[1]);
		
		ChessMove testMove;
		
		ChessPosition to;

		int timeMilli = 0;
		
		if (pos[0] < 7) {
			// row < 7, col =
			to = new ChessPosition(pos[0] + 1, pos[1]);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			testMoves.add(testMove);
			if (pos[1] < 7) {
				//row < 7 e col < 7
				to = new ChessPosition(pos[0] + 1, pos[1] + 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				testMoves.add(testMove);
				
				
			}
			
			if (pos[1] > 0) {
				//row < 7 e col > 0
				to = new ChessPosition(pos[0] + 1, pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				testMoves.add(testMove);
				
				to = new ChessPosition(pos[0], pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				testMoves.add(testMove);
			}
		}
		
		if (pos[0] > 0) {
			// row > 0, col =
			to = new ChessPosition(pos[0] - 1, pos[1]);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			testMoves.add(testMove);
			
			if (pos[1] < 7) {
				//row > 0 e col < 7
				to = new ChessPosition(pos[0] - 1, pos[1] + 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				testMoves.add(testMove);
			}
			
			if (pos[1] > 0) {
				//row > 0 e col > 0
				to = new ChessPosition(pos[0] - 1, pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				testMoves.add(testMove);
			}
		}
		
		if (pos[1] < 7) {
			//row =, col < 7
			to = new ChessPosition(pos[0], pos[1] + 1);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			testMoves.add(testMove);
		}
		
		if (pos[1] > 0) {
			//row =, col > 0
			to = new ChessPosition(pos[0], pos[1] - 1);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			testMoves.add(testMove);
		}
		
		if (pos[1] == 4) {
			to = new ChessPosition(pos[0], pos[1] + 2);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			testMoves.add(testMove);
			
			to = new ChessPosition(pos[0], pos[1] - 2);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			testMoves.add(testMove);
		}

		return testMoves;
	}
	
	private List<ChessMove> testAllRookMoves(ChessPiece[][] pieces, int[] pos) {
		
		ArrayList<ChessMove> testMoves = new ArrayList<ChessMove>();
		
		ChessPlayer testPlayer = new ChessPlayer("test","test@none");
		
		ChessPiece testPiece = pieces[pos[0]][pos[1]];
		
		ChessPosition from = new ChessPosition(pos[0],pos[1]);
		
		ChessMove testMove;
		
		ChessPosition to;

		int timeMilli = 0;
		
		
		for (int i = 0; i < 8; i++) {
			to = new ChessPosition(pos[0],i);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
			
			
			to = new ChessPosition(i,pos[1]);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
		}

		return testMoves;
	}
	
	private List<ChessMove> testAllBishopMoves(ChessPiece[][] pieces, int[] pos) {
		
		ArrayList<ChessMove> testMoves = new ArrayList<ChessMove>();
		
		ChessPlayer testPlayer = new ChessPlayer("test","test@none");
		
		ChessPiece testPiece = pieces[pos[0]][pos[1]];
		
		ChessPosition from = new ChessPosition(pos[0],pos[1]);
		
		ChessMove testMove;
		
		ChessPosition to;

		int timeMilli = 0;
		
		
		for (int i = pos[0], j = pos[1]; i < 8 && j < 8; i++, j++) {
			
			
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		for (int i = pos[0], j = pos[1]; i < 8 && j >= 0; i++,j--) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		for (int i = pos[0], j = pos[1]; i >= 0 && j < 8; i--,j++) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		for (int i = pos[0], j = pos[1]; i >= 0 && j >= 0; i--,j--) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		return testMoves;
	}

	private List<ChessMove> testAllQueenMoves(ChessPiece[][] pieces, int[] pos) {
		
		ArrayList<ChessMove> testMoves = new ArrayList<ChessMove>();
		
		ChessPlayer testPlayer = new ChessPlayer("test","test@none");
		
		ChessPiece testPiece = pieces[pos[0]][pos[1]];
		
		ChessPosition from = new ChessPosition(pos[0],pos[1]);
		
		ChessMove testMove;
		
		ChessPosition to;

		int timeMilli = 0;
		
		for (int i = 0; i < 8; i++) {
			to = new ChessPosition(pos[0],i);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
			
			
			to = new ChessPosition(i,pos[1]);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		
		
		for (int i = pos[0], j = pos[1]; i < 8 && j < 8; i++, j++) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		for (int i = pos[0], j = pos[1]; i < 8 && j >= 0; i++,j--) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		for (int i = pos[0], j = pos[1]; i >= 0 && j < 8; i--,j++) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		for (int i = pos[0], j = pos[1]; i >= 0 && j >= 0; i--,j--) {
			
				
			to = new ChessPosition(i,j);
			testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
			
			if(this.verifyToPosition(testMove)) {
				testMoves.add(testMove);
			}
			
		}
		
		return testMoves;
	}
	
	private List<ChessMove> testAllKnightMoves(ChessPiece[][] pieces, int[] pos) {
		
		ArrayList<ChessMove> testMoves = new ArrayList<ChessMove>();
		
		ChessPlayer testPlayer = new ChessPlayer("test","test@none");
		
		ChessPiece testPiece = pieces[pos[0]][pos[1]];
		
		ChessPosition from = new ChessPosition(pos[0],pos[1]);
		
		ChessMove testMove;
		
		ChessPosition to;

		int timeMilli = 0;
		
		to = new ChessPosition(pos[0] + 1, pos[1] + 2);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		
		to = new ChessPosition(pos[0] + 1, pos[1] - 2);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		to = new ChessPosition(pos[0] + 2, pos[1] + 1);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		to = new ChessPosition(pos[0] + 2, pos[1] - 1);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		
		to = new ChessPosition(pos[0] - 1, pos[1] + 2);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		to = new ChessPosition(pos[0] - 1, pos[1] - 2);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		to = new ChessPosition(pos[0] - 2, pos[1] + 1);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		to = new ChessPosition(pos[0] - 2, pos[1] - 1);
		testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
		if(this.verifyToPosition(testMove)) {
			testMoves.add(testMove);
		}
		
		return testMoves;
	}
	
	private List<ChessMove> testAllPawnMoves(ChessPiece[][] pieces, int[] pos) {
		
		ArrayList<ChessMove> testMoves = new ArrayList<ChessMove>();
		
		ChessPlayer testPlayer = new ChessPlayer("test","test@none");
		
		ChessPiece testPiece = pieces[pos[0]][pos[1]];
		
		ChessPosition from = new ChessPosition(pos[0],pos[1]);
		
		ChessMove testMove;
		
		ChessPosition to;

		int timeMilli = 0;
		
		if (testPiece.getColor() == Color.WHITE) {
			
			if (pos[0] < 6) {
				if(pos[0] == 1){
					to = new ChessPosition(pos[0] + 2 ,pos[1]);
					testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
					if(this.verifyToPosition(testMove)) {
						testMoves.add(testMove);
					}
				}
				to = new ChessPosition(pos[0] + 1 ,pos[1]);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] + 1 ,pos[1] + 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] + 1 ,pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
			} else {
				
				to = new ChessPosition(pos[0] + 1 ,pos[1]);
				testMove = new ChessMove(testPlayer, testPiece, from, to, ChessPieceKind.QUEEN, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] + 1 ,pos[1] + 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, ChessPieceKind.QUEEN, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] + 1 ,pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, ChessPieceKind.QUEEN, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
			}

		} else if (testPiece.getColor() == Color.BLACK) {
			
			if (pos[0] > 1) {
				if(pos[0] == 6){
					to = new ChessPosition(pos[0] - 2 ,pos[1]);
					testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
					if(this.verifyToPosition(testMove)) {
						testMoves.add(testMove);
					}
				}

				to = new ChessPosition(pos[0] - 1 ,pos[1]);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] - 1 ,pos[1] + 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] - 1 ,pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
			} else {
				
				to = new ChessPosition(pos[0] - 1 ,pos[1]);
				testMove = new ChessMove(testPlayer, testPiece, from, to, ChessPieceKind.QUEEN, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] - 1 ,pos[1] + 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, ChessPieceKind.QUEEN, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
				to = new ChessPosition(pos[0] - 1 ,pos[1] - 1);
				testMove = new ChessMove(testPlayer, testPiece, from, to, ChessPieceKind.QUEEN, timeMilli);
				if(this.verifyToPosition(testMove)) {
					testMoves.add(testMove);
				}
				
			}
			
		}
		
		return testMoves;
	}

	private boolean testPieceMove(List<ChessMove> testMoves, ChessPieceKind piece){
		for (int o = 0; o < testMoves.size(); o++) {
			try{
				ChessMove move = testMoves.get(o);
				boolean result = true;
				switch (piece) {
					case KING:
						result = this.testKingMove(move);
						break;
					case ROOK:
						result = this.testRookMove(move);
						break;
					case BISHOP:
						result = this.testBishopMove(move);
						break;
					case QUEEN:
						result = this.testQueenMove(move);
						break;
					case KNIGHT:
						result = this.testKnightMove(move);
						break;
					case PAWN:
						result = this.testPawnMove(move);
						break;
				}
				if(result){
					return false;
				}
			} catch (IllegalMoveException e) {}
		}
		return true;
	}

	public List<ChessMove> getPossibleMoves(ChessPosition position){
		ChessPiece piece = this.pieces[position.getRow()][position.getCol()];
		List<ChessMove> result = null;
		List<ChessMove> returnList = new ArrayList<ChessMove>();

		if(piece != null){
			switch (piece.getChessPieceKind()) {
				case KING:
					result = this.testAllKingMoves(this.pieces, position.getArrayPosition());
					for(int i = 0; i < result.size(); i++){
						try{
							if(this.testKingMove(result.get(i))){
								returnList.add(result.get(i));
							}
						} catch (IllegalMoveException e) {}
					}
					break;
				case ROOK:
					result = this.testAllRookMoves(this.pieces, position.getArrayPosition());
					for(int i = 0; i < result.size(); i++){
						try{
							if(this.testRookMove(result.get(i))){
								returnList.add(result.get(i));
							}
						} catch (IllegalMoveException e) {}
					}
					break;
				case BISHOP:
					result = this.testAllBishopMoves(this.pieces, position.getArrayPosition());
					for(int i = 0; i < result.size(); i++){
						try{
							if(this.testBishopMove(result.get(i))){
								returnList.add(result.get(i));
							}
						} catch (IllegalMoveException e) {}
					}
					break;
				case QUEEN:
					result = this.testAllQueenMoves(this.pieces, position.getArrayPosition());
					for(int i = 0; i < result.size(); i++){
						try{
							if(this.testQueenMove(result.get(i))){
								returnList.add(result.get(i));
							}
						} catch (IllegalMoveException e) {}
					}
					break;
				case KNIGHT:
					result = this.testAllKnightMoves(this.pieces, position.getArrayPosition());
					for(int i = 0; i < result.size(); i++){
						try{
							if(this.testKnightMove(result.get(i))){
								returnList.add(result.get(i));
							}
						} catch (IllegalMoveException e) {}
					}
					break;
				case PAWN:
					result = this.testAllPawnMoves(this.pieces, position.getArrayPosition());
					for(int i = 0; i < result.size(); i++){
						try{
							if(this.testPawnMove(result.get(i))){
								returnList.add(result.get(i));
							}
						} catch (IllegalMoveException e) {}
					}
					break;
			}
		}

		return returnList;
	}

	public boolean isFinished() {
		return this.finished;
	}
	
	public Color getWinner() {
		return this.winner;
	}
	
	public Color getTurn() {
		return this.turn;
	}
	
	public void finishGame(Color winner) {
		this.finished = true;
		if (winner != null) {
			this.winner = winner;
		}
	}
	
	public ArrayList<Integer> getNumberPiecesCreated() {
		
		return this.numberPiecesCreated;
	}
	
	public ArrayList<Integer> getNumberEatenPieces(){
		int nWhitePawns = 0;
		int nWhiteRooks = 0;
		int nWhiteKnights = 0;
		int nWhiteBishops = 0;
		int nWhiteQueens = 0;
		
		int nBlackPawns = 0;
		int nBlackRooks = 0;
		int nBlackKnights = 0;
		int nBlackBishops = 0;
		int nBlackQueens = 0;
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (this.pieces[i][j] != null){
					if (this.pieces[i][j].getColor().equals(Color.WHITE)) {
						
						switch(this.pieces[i][j].getChessPieceKind()) {
							case PAWN:
								nWhitePawns++;
								break;
							case ROOK:
								nWhiteRooks++;
								break;
							case KNIGHT:
								nWhiteKnights++;
								break;
							case BISHOP:
								nWhiteBishops++;
								break;
							case QUEEN:
								nWhiteQueens++;
								break;
							case KING:
								break;
						}
						
						
					} else if (this.pieces[i][j].getColor().equals(Color.BLACK)) {
						
						switch(this.pieces[i][j].getChessPieceKind()) {
							case PAWN:
								nBlackPawns++;
								break;
							case ROOK:
								nBlackRooks++;
								break;
							case KNIGHT:
								nBlackKnights++;
								break;
							case BISHOP:
								nBlackBishops++;
								break;
							case QUEEN:
								nBlackQueens++;
								break;
							case KING:
								break;
						}
						
						
					}
				}
			}
		}
		
		ArrayList<Integer> eatenPieces = new ArrayList<Integer>();
		
		eatenPieces.add(this.numberPiecesCreated.get(0) - nWhitePawns);
		eatenPieces.add(this.numberPiecesCreated.get(1) - nWhiteRooks);
		eatenPieces.add(this.numberPiecesCreated.get(2) - nWhiteKnights);
		eatenPieces.add(this.numberPiecesCreated.get(3) - nWhiteBishops);
		eatenPieces.add(this.numberPiecesCreated.get(4) - nWhiteQueens);
		
		eatenPieces.add(this.numberPiecesCreated.get(5) - nBlackPawns);
		eatenPieces.add(this.numberPiecesCreated.get(6) - nBlackRooks);
		eatenPieces.add(this.numberPiecesCreated.get(7) - nBlackKnights);
		eatenPieces.add(this.numberPiecesCreated.get(8) - nBlackBishops);
		eatenPieces.add(this.numberPiecesCreated.get(9) - nBlackQueens);
		
		return eatenPieces;
	}
	
	public String getBoardEndDescription() {
		return this.boardEndDescription;
	}
	
	public void setBoardEndDescription(String description) {
		this.boardEndDescription = description;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("  ");
		
		for (int i = 0; i < 8; i++) {
			
			sb.append("   ").append((char) ('a' + i)).append(" "); 
		}
		
	    sb.append("\n  +");
	
	    for (int i = 0; i < 8; i++) {
	    	
	    	sb.append("----+");
	    }
	    
	    sb.append("\n");
	    
	    for (int i = 7; i >= 0; i--) {
	    	
	    	sb.append("").append(i+1).append(" ");
	    	
	    	for (int j = 0; j < 8; j++) {
	    		
	    		if (get(i, j) != null) {
	    			
	    			sb.append("| ").append(get(i, j)).append(" ");
	    			
	    		} else {
	    			
	    			sb.append("|    ");
	    		}
	    	}
	    	sb.append("|\n  +");
	    	
	    	for (int k = 0; k < 8; k++) {
	    		
	    		sb.append("----+");
	        }
	    	
	        sb.append("\n");
	    }
	    
	    return sb.toString(); 
	}
	
	public String toHtml() {
		String original = this.toString();
		String result = original.replace("\n", "<br>");
		result = result.replace(" ", "&nbsp;");
		return result;
	}
	
	public String getPieceHtml(int row, int col) {
		if (this.pieces[row][col] != null) {
			return this.pieces[row][col].toHtml();
		}
		return " ";
	}
	
	private String viewArray(ChessPiece[][]  cp) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("ARRAY VIEW \n");
		
		sb.append("  ");
		
		for (int i = 0; i < 8; i++) {
			
			sb.append("   ").append((char) ('a' + i)).append(" "); 
		}
		
	    sb.append("\n  +");
	
	    for (int i = 0; i < 8; i++) {
	    	
	    	sb.append("----+");
	    }
	    
	    sb.append("\n");
	    
	    for (int i = 7; i >= 0; i--) {
	    	
	    	sb.append("").append(i+1).append(" ");
	    	
	    	for (int j = 0; j < 8; j++) {
	    		
	    		if (cp[i][j] != null) {
	    			
	    			sb.append("| ").append(cp[i][j]).append(" ");
	    			
	    		} else {
	    			
	    			sb.append("|    ");
	    		}
	    	}
	    	sb.append("|\n  +");
	    	
	    	for (int k = 0; k < 8; k++) {
	    		
	    		sb.append("----+");
	        }
	    	
	        sb.append("\n");
	    }
	    
	    return sb.toString(); 
	}
}

