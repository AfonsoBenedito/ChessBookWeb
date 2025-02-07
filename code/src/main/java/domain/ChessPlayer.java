package domain;

import javax.persistence.*;

@Entity
@NamedQueries({
	@NamedQuery(name = "ChessPlayer.findByName",
				query = "SELECT cp FROM ChessPlayer cp WHERE cp.name = :name"),
	@NamedQuery(name = "ChessPlayer.findByEmail",
				query = "SELECT cp FROM ChessPlayer cp WHERE cp.email = :email"),
	@NamedQuery(name = "ChessPlayer.findPlayerList",
				query = "SELECT cp FROM ChessPlayer cp"),
	@NamedQuery(name = "ChessPlayer.findLikeName",
				query = "SELECT cp FROM ChessPlayer cp WHERE cp.name LIKE '%:name%'")
})
@Table(name="ChessPlayer")
public class ChessPlayer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String name;

	private String email;
		
	public ChessPlayer() {}
	
	public ChessPlayer(String name, String email){
		this.name = name;
		this.email = email;
	}

	public int getId() { return this.id; }
	
	public void setId(int id) { this.id = id; }
 	
	public String getName() { return this.name; }
	
	public String getEmail() { return this.email; }
	
	public void setEmail(String e) { this.email = e; }
	
	public void setName(String name) { this.name = name; }
	
	public boolean equals(String name, String email) { return this.name.equals(name) && this.email.equals(email); }
}