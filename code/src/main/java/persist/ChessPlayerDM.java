package persist;
import java.util.List;
import java.util.Optional;
import javax.persistence.*;
import domain.*;
import main.ChessMain;

public class ChessPlayerDM extends ADataMapper<ChessPlayer> {
	
	private static ChessPlayerDM single_instance = null;
			
	private ChessPlayerDM(){
        super(ChessPlayer.class);
    }
	
	public static ChessPlayerDM getInstance() {
		if (single_instance == null) {
			single_instance = new ChessPlayerDM();
		}
		return single_instance;
	}
	
	public List<ChessPlayer> findByName(String name) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessPlayer> query = em.createNamedQuery("ChessPlayer.findByName", ChessPlayer.class);
		
		query.setParameter("name", name);
		
		List<ChessPlayer> result = query.getResultList();
		em.close();

		return result;
	}
	
	public Optional<ChessPlayer> findByEmail(String email) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessPlayer> query = em.createNamedQuery("ChessPlayer.findByEmail", ChessPlayer.class);
		
		query.setParameter("email", email);
		
		Optional<ChessPlayer> result;
		
		try {
			result = Optional.of(query.getSingleResult());
		} catch (NoResultException e) {
			result = Optional.empty();
		}
		
		em.close();

		return result;
	}
	
	public List<ChessPlayer> chessPlayersList(){
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessPlayer> query = em.createNamedQuery("ChessPlayer.findPlayerList", ChessPlayer.class);

		List<ChessPlayer> result = query.getResultList();
		em.close();

		return result;
	}
	
	public void deleteAllPlayers() {
		
		EntityManager em = ChessMain.getEntityManager();
		//em.createNamedQuery("ChessPlayer.deleteAllPlayers", ChessPlayer.class);
		em.getTransaction().begin();
		em.createNativeQuery("DELETE FROM ChessPlayer").executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
	
	public List<ChessPlayer> findLikeName(String name) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessPlayer> query = em.createNamedQuery("ChessPlayer.findLikeName", ChessPlayer.class);
		
		query.setParameter("name", name);
		
		List<ChessPlayer> result = query.getResultList();
		em.close();

		return result;
	}
}
