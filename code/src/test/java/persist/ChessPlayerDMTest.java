package persist;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import domain.*;

public class ChessPlayerDMTest {

	private ChessGameDM cgDM;
	private ChessPlayerDM cpDM;
	
	@BeforeEach
	public void beforeAll() {
		cgDM = ChessGameDM.getInstance();
		cgDM.deleteAllGames();

		cpDM = ChessPlayerDM.getInstance();
		cpDM.deleteAllPlayers();
	}
	
	//VERIFICAR QUANTOS SE VAI BUSCAR LISTA CERTA
	@Test
	@Order(1)
	public void testChessPlayersList() {
		List<ChessPlayer> l = cpDM.chessPlayersList();
		
		assertEquals(l.size(),0, "Should be 0.");
	}
	
	@Test
	@Order(2)
	public void testInsertPlayer() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		
		cpDM.insert(playerOne);
		List<ChessPlayer> l = cpDM.chessPlayersList();
		
		assertEquals(l.get(0).getEmail(), playerOne.getEmail());
	}
	
	@Test
	@Order(3)
	public void testFindByPlayerName() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");

		cpDM.insert(playerOne);
		List<ChessPlayer> list = cpDM.findByName("Afonso");
		
		assertEquals(list.get(0).getName(), playerOne.getName());
	}
	
	@Test
	@Order(4)
	public void testFindByPlayerEmail() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		
		cpDM.insert(playerOne);
		Optional<ChessPlayer> list = cpDM.findByEmail("afonso@gmail");
		
		assertEquals(list.get().getEmail(),playerOne.getEmail());
	}
	
	@Test
	@Order(5)
	public void testUpdatePlayer() {
		
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		cpDM.insert(playerOne);
		
		playerOne.setName("Joao");
		cpDM.update(playerOne);
		
		List<ChessPlayer> l = cpDM.findByName("Joao");
		
		assertEquals(l.get(0).getEmail(), playerOne.getEmail());
	}
	
	@Test
	@Order(6)
	public void testRemovePlayer() {
		
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		cpDM.insert(playerOne);
		
		cpDM.remove(playerOne);
		List<ChessPlayer> l = cpDM.chessPlayersList();
		
		assertEquals(l.size(), 0);
	}
}
