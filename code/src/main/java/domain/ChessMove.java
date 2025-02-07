package domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="ChessMove")
public class ChessMove implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name= "idMove")
	private int id;
	
	@Embedded
	@AttributeOverrides( {
        @AttributeOverride(name="row", column = @Column(name="fromRow") ),
        @AttributeOverride(name="col", column = @Column(name="fromCol") )
	} )
	private ChessPosition from;
	
	@Embedded
	@AttributeOverrides( {
        @AttributeOverride(name="row", column = @Column(name="toRow") ),
        @AttributeOverride(name="col", column = @Column(name="toCol") )
	} )
	private ChessPosition to;
	
	@Embedded
	private ChessPiece cp;

	private ChessPieceKind promotion;
	
	@OneToOne
	private ChessPlayer player;

	private int timeMilli;
	
	public ChessMove() {}
	
	public ChessMove(ChessPlayer p, ChessPiece cp, ChessPosition from, ChessPosition to, int timeMilli) {
		this.player = p;
		this.cp = cp;
		this.from = from;
		this.to = to;
		this.timeMilli = timeMilli;
	}
	
	public ChessMove(ChessPlayer p, ChessPiece cp, ChessPosition from, ChessPosition to, ChessPieceKind promotion, int timeMilli) {
		this.player = p;
		this.cp = cp;
		this.from = from;
		this.to = to;
		this.promotion = promotion;
		this.timeMilli = timeMilli;
	}
	
	public int getId() { return this.id; }
	
	public ChessPlayer getPlayer() { return this.player; }
	
	public ChessPosition getFrom() { return this.from; }
	
	public ChessPosition getTo() { return this.to; }
	
	public ChessPiece getPiece() { return this.cp; }
	
	public ChessPieceKind getPromotion() { return this.promotion; }

	public int getTimeMilli(){ return this.timeMilli;}
	
	public void setId(int id) { this.id = id; }
	
	public void setPlayer(ChessPlayer p) { this.player = p; }
	
	public void setFrom(ChessPosition f) { this.from = f; }
	
	public void setTo(ChessPosition t) { this.to = t; }
	
	public void setPiece(ChessPiece cp) { this.cp = cp; }
	
	public void setPromotion(ChessPieceKind p) { this.promotion = p; }

	public void setTimeMilli(int timeMilli){ this.timeMilli = timeMilli; }
}