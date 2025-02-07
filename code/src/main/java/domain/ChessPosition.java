package domain;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class ChessPosition implements Serializable{
	
	@Column
	private int row;
	
	@Column
	private int col;
	
	public ChessPosition() {}
	
	public ChessPosition(int r, int c) {
		this.row = r;
		this.col = c;
	}
	
	public int getRow() { return this.row; }
	
	public int getCol() {
		return this.col;
	}
	
	public int[] getArrayPosition() {
		int[] result = {this.row, this.col};
		
		return result;
	}
}