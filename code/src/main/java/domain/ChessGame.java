package domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@NamedQueries({
	@NamedQuery(name = "ChessGame.findByWinner",
				query = "SELECT cg FROM ChessGame cg WHERE cg.winner = :winner"),
	@NamedQuery(name = "ChessGame.findFinished",
				query = "SELECT cg FROM ChessGame cg WHERE cg.finished = true"),
	@NamedQuery(name = "ChessGame.findByPlayerName", 
				query = "SELECT cg FROM ChessGame cg, ChessPlayer p WHERE ((cg.black = p) OR (cg.white = p)) AND (p.name =:name)"),
	@NamedQuery(name = "ChessGame.findByPlayerEmail", 
				query = "SELECT cg FROM ChessGame cg, ChessPlayer p WHERE ((cg.black = p) OR (cg.white = p)) AND (p.email =:email)"),
	@NamedQuery(name = "ChessGame.findByBlack",
				query = "SELECT cg FROM ChessGame cg WHERE cg.black = :black"),
	@NamedQuery(name = "ChessGame.findByWhite",
				query = "SELECT cg FROM ChessGame cg WHERE cg.white = :white"),
	@NamedQuery(name = "ChessGame.findGamesList",
				query = "SELECT cg FROM ChessGame cg"),
	@NamedQuery(name = "ChessGame.findFinishedGamesByEmail",
				query = "SELECT cg FROM ChessGame cg, ChessPlayer p WHERE (((cg.black = p) OR (cg.white = p)) AND (p.email =:email) AND cg.finished = true)"),
	@NamedQuery(name = "ChessGame.findUnfinishedGamesByEmail",
				query = "SELECT cg FROM ChessGame cg, ChessPlayer p WHERE (((cg.black = p) OR (cg.white = p)) AND (p.email =:email) AND cg.finished = false)"),
})
@Table(name="ChessGame")
public class ChessGame{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne
	@JoinColumn(nullable = false, name="white_ChessPlayer_id")
    private ChessPlayer white;
	
	@ManyToOne
	@JoinColumn(nullable = false, name="black_ChessPlayer_id")
    private ChessPlayer black;
	
	@OneToMany(cascade=CascadeType.ALL)
    private List<ChessMove> moves;
	
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date openDate;
    
    private boolean finished;
    
    @ManyToOne
	@JoinColumn(nullable = true, name="winner_ChessPlayer_id")
    private ChessPlayer winner;
    
    @Transient
    private ChessBoard board;

    private ChessPlayer tieOffer;
    
    private boolean whiteNotification;
    
    private boolean blackNotification;
    
    private String endGameDescription;
    
    public ChessGame(){}
    
    public ChessGame(ChessPlayer white, ChessPlayer black, Date startDate) {
    	this.white = white;
    	this.black = black;
    	
    	this.moves = new ArrayList<ChessMove>();
    	this.date = startDate;
    	this.finished = false;
    	
    	this.board = new ChessBoard();
    	this.tieOffer = null;
    	
    	
    	this.whiteNotification = false;
    	this.blackNotification = false;
    	
    	this.endGameDescription = null;
    }
    
    public int getId() { return this.id; }
    
    public void setId(int id) { this.id = id; }
    
    public ChessPlayer getWhite() { return this.white; }
    
    public void setWhite(ChessPlayer w) { this.white = w; }
    
    public ChessPlayer getBlack() { return this.black; }
    
    public void setBlack(ChessPlayer b) { this.black = b; }
    
    public ChessBoard getBoard() { return this.board; }
    
    public void setBoard(ChessBoard b) { this.board = b; }
    
    public List<ChessMove> getMoves(){ return this.moves; }
    
    public Date getDate() { return this.date; }
    
    public ChessPlayer getWinner() { if (this.finished) {
    		return this.winner;
    	}
    	return null;
    }
    
    public Date getOpenDate(){ return this.openDate; }
    
    public boolean getWhiteNotification() { return this.whiteNotification; }
    
    public boolean getBlackNotification() { return this.blackNotification; }
    
    public String getEndGameDescription() { return this.endGameDescription; }
    
    public void setEndGameDescription(String description) { this.endGameDescription = description; }
    
    public void setWhiteNotification(boolean bool) { this.whiteNotification = bool; }
    
    public void setBlackNotification(boolean bool) { this.blackNotification = bool; }

    public void setOpenDate(Date newDate){ this.openDate = newDate; }
    
    public ChessPlayer getTieOffer() { return this.tieOffer; }
    
    public void setTieOffer(ChessPlayer player) { this.tieOffer = player; }
    
    public void offerDraw(ChessPlayer player) { this.tieOffer = player; }
    
    public void acceptDraw() { this.stalemate(); }
    
    public void refuseDraw() { this.tieOffer = null; }
    
    public boolean addMove(ChessMove mv) throws IllegalMoveException {
    	
    	this.board.update(mv);
    	
    	this.moves.add(mv);
    	
    	if (this.board.isFinished()) {
    		this.finished = true;
    		if (this.board.getWinner() != null) {
    			if (this.board.getWinner().equals(Color.WHITE)) {
        			this.winner = this.getWhite();
        			System.out.println("White Won");
        		} else if (this.board.getWinner().equals(Color.BLACK)) {
        			this.winner = this.getBlack();
        			System.out.println("Black Won");
        		}
    		}
    		this.endGameDescription = this.board.getBoardEndDescription();
    	}
    	
    	this.openDate = null;
    	
    	this.tieOffer = null;
    	
    	return true;
    }
    
    public void whiteResign() {
    	this.finished = true;
    	this.winner = this.getBlack();
    	this.board.finishGame(Color.BLACK);
    	this.endGameDescription = "White Resigned";
    }

    public void blackResign() {
    	this.finished = true;
    	this.winner = this.getWhite();
    	this.board.finishGame(Color.WHITE);
    	this.endGameDescription = "Black Resigned";
    }

    public void stalemate() {
    	this.finished = true;
    	this.board.finishGame(null);
    	this.endGameDescription = "Players agreed a Draw";
    }

	public List<String> getPossibleMoves(String square){
		ChessPosition position = null;
		switch (square.charAt(0)) {
			case 'a':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,0);
				break;
			case 'b':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,1);
				break;
			case 'c':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,2);
				break;
			case 'd':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,3);
				break;
			case 'e':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,4);
				break;
			case 'f':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,5);
				break;
			case 'g':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,6);
				break;
			case 'h':
				position = new ChessPosition(Character.getNumericValue(square.charAt(1)) - 1,7);
				break;
			default:
				position = null;
		}

		List<ChessMove> possibleMoves = this.board.getPossibleMoves(position);
		List<String> result = new ArrayList<>();
		for(int i = 0; i < possibleMoves.size(); i++){
			switch(possibleMoves.get(i).getTo().getCol()){
				case 0:
					result.add("a" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 1:
					result.add("b" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 2:
					result.add("c" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 3:
					result.add("d" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 4:
					result.add("e" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 5:
					result.add("f" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 6:
					result.add("g" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
				case 7:
					result.add("h" + (possibleMoves.get(i).getTo().getRow() + 1));
					break;
			}
		}

		return result;
	}

    public String totalTime(Color c) {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("hh:mm:ss");
    	
    	return sb.toString();
    }
    
    public ChessMove getLastMove() {
    	return this.moves.get(this.moves.size() - 1);
    }
    
    public boolean isFinished() {
    	return this.finished;
    }
    
    public ChessPlayer getPlayerTurn() {
    	if(this.board.getTurn().equals(Color.WHITE)) {
    		return this.getWhite();
    	}
    	return this.getBlack();
    }
    
    public boolean verifyInput(String input) {
    	String[] strList = input.split(" ");
    	
    	
    	ArrayList<Character> possibleCols = new ArrayList<Character>();
    	possibleCols.add('a');
    	possibleCols.add('b');
    	possibleCols.add('c');
    	possibleCols.add('d');
    	possibleCols.add('e');
    	possibleCols.add('f');
    	possibleCols.add('g');
    	possibleCols.add('h');
    	
    	ArrayList<Character> possibleRows = new ArrayList<Character>();
    	possibleRows.add('1');
    	possibleRows.add('2');
    	possibleRows.add('3');
    	possibleRows.add('4');
    	possibleRows.add('5');
    	possibleRows.add('6');
    	possibleRows.add('7');
    	possibleRows.add('8');
    	
    	if (strList.length <= 3 && strList.length >= 2) {
    		if (strList[0].length() == 2 && strList[1].length() == 2) {
    			if (possibleCols.contains(strList[0].charAt(0)) && possibleRows.contains(strList[0].charAt(1))) {
    				if (possibleCols.contains(strList[1].charAt(0)) && possibleRows.contains(strList[1].charAt(1))) {
    					if (strList.length == 3) {
    						if (strList[2].equals("QUEEN") || strList[2].equals("ROOK") || strList[2].equals("BISHOP") || strList[2].equals("KNIGHT")) {
        						return true;
        					}
    					} else {
    						return true;
    					}
        			}
    			}
    		}
    	}
    
    	return false;
    }
    
    public ChessMove convertInputToMove(String input, ChessPlayer player, ChessGame game, int timeMilli) {
    	
    	String[] strList = input.split(" ");
    	
    	ChessPosition from;
    	ChessPosition to;
    	
    	ChessMove move = null;
    	
    	switch (strList[0].charAt(0)) {
			case 'a':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,0);
				break;

			case 'b':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,1);
				break;

			case 'c':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,2);
				break;

			case 'd':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,3);
				break;

			case 'e':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,4);
				break;

			case 'f':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,5);
				break;

			case 'g':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,6);
				break;

			case 'h':
				from = new ChessPosition(Character.getNumericValue(strList[0].charAt(1)) - 1,7);
				break;

			default:
				from = null;
    	}
    	
    	switch (strList[1].charAt(0)) {
			case 'a':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,0);
				break;

			case 'b':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,1);
				break;

			case 'c':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,2);
				break;

			case 'd':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,3);
				break;

			case 'e':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,4);
				break;

			case 'f':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,5);
				break;

			case 'g':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,6);
				break;

			case 'h':
				to = new ChessPosition(Character.getNumericValue(strList[1].charAt(1)) - 1,7);
				break;

			default:
				to = null;
		}
    	
    	
    	if (strList.length == 2) {
    		move = new ChessMove(player,game.getBoard().get(from.getRow(), from.getCol()), from, to, timeMilli);
    	} else if (strList.length == 3) {
    		
    		if (strList[2].equals("QUEEN")) {
    			move = new ChessMove(player,game.getBoard().get(from.getRow(), from.getCol()), from, to, ChessPieceKind.QUEEN, timeMilli);
    		} else if (strList[2].equals("ROOK")) {
    			move = new ChessMove(player,game.getBoard().get(from.getRow(), from.getCol()), from, to, ChessPieceKind.ROOK, timeMilli);
    		} else if (strList[2].equals("BISHOP")) {
    			move = new ChessMove(player,game.getBoard().get(from.getRow(), from.getCol()), from, to, ChessPieceKind.BISHOP, timeMilli);
    		} else if (strList[2].equals("KNIGHT")) {
    			move = new ChessMove(player,game.getBoard().get(from.getRow(), from.getCol()), from, to, ChessPieceKind.KNIGHT, timeMilli);
    		}

    	}
    	
    	return move;
    }
}