package persist;

import java.util.List;

import javax.persistence.*;
import domain.*;
import main.ChessMain;

public class ChessGameDM extends ADataMapper<ChessGame>{
	
	private static ChessGameDM single_instance = null;
	
    private ChessGameDM(){ super(ChessGame.class); }
    
    public static ChessGameDM getInstance() {
		if (single_instance == null) {
			single_instance = new ChessGameDM();
		}
		return single_instance;
	}

    public List<ChessGame> findByWinner(ChessPlayer winner) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findByWinner", ChessGame.class);
		
		query.setParameter("winner", winner);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findFinished() {
    	EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findFinished", ChessGame.class);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findByBlack(ChessPlayer black) {
    	EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findByBlack", ChessGame.class);
		
		query.setParameter("player", black);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findByWhite(ChessPlayer white) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findByWhite", ChessGame.class);
		
		query.setParameter("player", white);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findByPlayerName(String name) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findByPlayerName", ChessGame.class);
		
		query.setParameter("name", name);
		
		List<ChessGame> result = query.getResultList();
		em.close();
		
		return result;
	}
    
    public List<ChessGame> findByPlayerEmail(String email) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findByPlayerEmail", ChessGame.class);
		
		query.setParameter("email", email);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findGamesList() {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findGamesList", ChessGame.class);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findFinishedGamesByEmail(String email) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findFinishedGamesByEmail", ChessGame.class);
		
		query.setParameter("email", email);
		
		List<ChessGame> result = query.getResultList();
		em.close();

		return result;
	}
    
    public List<ChessGame> findUnfinishedGamesByEmail(String email) {
		EntityManager em = ChessMain.getEntityManager();
		TypedQuery<ChessGame> query = em.createNamedQuery("ChessGame.findUnfinishedGamesByEmail", ChessGame.class);
		
		query.setParameter("email", email);
		
		List<ChessGame> result = query.getResultList();
		em.close();
		
		return result;
	}
    
    public void deleteAllGames() {
		EntityManager em = ChessMain.getEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM ChessGame").executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
}