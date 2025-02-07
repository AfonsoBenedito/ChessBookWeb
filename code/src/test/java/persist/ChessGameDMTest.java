package persist;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.junit.jupiter.api.*;
import domain.*;
import main.ChessMain;

public class ChessGameDMTest {

	private ChessGameDM cgDM;
	private ChessPlayerDM cpDM;

	@BeforeEach
	public void beforeAll() {
		try {
			EntityManager em = ChessMain.getEntityManager();
		} catch (Exception e) {
			System.out.println("An error occured while creating the EntityManagerFactory");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		cgDM = ChessGameDM.getInstance();
		cgDM.deleteAllGames();
		cpDM = ChessPlayerDM.getInstance();
		cpDM.deleteAllPlayers();
	}
	
	
	@Test
	@Order(1)
	public void testChessGamesList1() {
		List<ChessGame> l = cgDM.findGamesList();
		assertEquals(l.size(),0, "Should be 0.");
	}
	
	@Test
	@Order(2)
	public void testInsertGame() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		ChessPlayer playerTwo = new ChessPlayer("Tomas","tomas@gmail");
		cpDM.insert(playerOne);
		cpDM.insert(playerTwo);
		
		Date d = new Date(System.currentTimeMillis());
		
		ChessGame game = new ChessGame(playerOne, playerTwo, d);
		
		cgDM.insert(game);
		
		List<ChessGame> l = cgDM.findGamesList();
		
		assertEquals(l.size(),1, "Should be 1.");
	}
	
	@Test
	@Order(3)
	public void testFindByPlayerName() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		ChessPlayer playerTwo = new ChessPlayer("Tomas","tomas@gmail");
		cpDM.insert(playerOne);
		cpDM.insert(playerTwo);
		
		Date d = new Date(System.currentTimeMillis());
		
		ChessGame game = new ChessGame(playerOne, playerTwo, d);
		
		cgDM.insert(game);
		
		List<ChessGame> list = cgDM.findByPlayerName("Afonso");
		
		assertEquals(list.get(0).getWhite().getEmail(), playerOne.getEmail());
	}
	
	@Test
	@Order(4)
	public void testFindByPlayerEmail() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		ChessPlayer playerTwo = new ChessPlayer("Tomas","tomas@gmail");
		cpDM.insert(playerOne);
		cpDM.insert(playerTwo);
		
		Date d = new Date(System.currentTimeMillis());
		
		ChessGame game = new ChessGame(playerOne, playerTwo, d);
		
		cgDM.insert(game);
		
		List<ChessGame> list = cgDM.findByPlayerEmail("afonso@gmail");
		
		assertEquals(list.get(0).getWhite().getName(), playerOne.getName());
	}
	
	@Test
	@Order(5)
	public void testUpdateGame() {
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		ChessPlayer playerTwo = new ChessPlayer("Tomas","tomas@gmail");
		cpDM.insert(playerOne);
		cpDM.insert(playerTwo);
		
		Date d = new Date(System.currentTimeMillis());
		
		ChessGame game = new ChessGame(playerOne, playerTwo, d);
		
		cgDM.insert(game);
		
		ChessPlayer playerThree = new ChessPlayer("Manuel","acacio@gmail");
		
		game.setBlack(playerThree);
		
		cgDM.update(game);
		
		List<ChessGame> l = cgDM.findGamesList();

		assertEquals(l.get(0).getBlack().getEmail(), playerThree.getEmail());
	}
	
	
	@Test
	@Order(6)
	public void testRemovePlayer() {
		
		ChessPlayer playerOne = new ChessPlayer("Afonso","afonso@gmail");
		ChessPlayer playerTwo = new ChessPlayer("Tomas","tomas@gmail");
		cpDM.insert(playerOne);
		cpDM.insert(playerTwo);
		
		Date d = new Date(System.currentTimeMillis());
		
		ChessGame game = new ChessGame(playerOne, playerTwo, d);
		
		cgDM.insert(game);
		
		cgDM.remove(game);
		
		List<ChessGame> l = cgDM.findGamesList();
		
		assertEquals(l.size(), 0);
	}
}
