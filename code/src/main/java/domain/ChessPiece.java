package domain;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class ChessPiece implements Serializable{

	private int id;
	
	@Column(name="Color")
	private Color color;

	@Column(name="PieceType")
	private ChessPieceKind pieceType;

	@Transient
	private int numberOfMoves;
	
	public ChessPiece() {}
	
	public ChessPiece(ChessPieceKind pT, Color c) {
		this.pieceType = pT;
		this.color = c;
		this.numberOfMoves = 0;
	}
	
	public Color getColor() { return this.color; }
	
	public ChessPieceKind getPieceType() { return this.pieceType; }

	public ChessPieceKind getChessPieceKind() { return this.pieceType; }
	
	public int getNumberOfMoves() { return this.numberOfMoves; }
	
	public void setChessPieceKind(ChessPieceKind kind) { this.pieceType = kind; }
	
	public void setColor(Color color) { this.color = color; }
	
	public void setNumberOfMoves(int number) { this.numberOfMoves = number; }
	
	public String toHtml() {
		if (this.color == Color.WHITE) {
			switch (this.pieceType) {
				case PAWN:
					return "&#9817;";

				case ROOK:
					return "&#9814;";

				case KNIGHT:
					return "&#9816;";

				case BISHOP:
					return "&#9815;";

				case QUEEN:
					return "&#9813;";

				case KING:
					return "&#9812;";
			}
		} else if (this.color == Color.BLACK) {
			switch (this.pieceType) {
				case PAWN:
					return "&#9823;";

				case ROOK:
					return "&#9820;";

				case KNIGHT:
					return "&#9822;";

				case BISHOP:
					return "&#9821;";

				case QUEEN:
					return "&#9819;";

				case KING:
					return "&#9818;";
			}
		}
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		switch (this.pieceType) {
			case PAWN:
				sb.append('P');
				break;
				
			case ROOK:
				sb.append('R');
				break;
				
			case KNIGHT:
				sb.append('k');
				break;
				
			case BISHOP:
				sb.append('B');
				break;
				
			case QUEEN:
				sb.append('Q');
				break;
				
			case KING:
				sb.append('K');
				break;
		}

		switch (this.color) {
			case WHITE:
				sb.append('w');
				break;
				
			case BLACK:
				sb.append('b');
				break;
		}
		
		return sb.toString();
	}
}