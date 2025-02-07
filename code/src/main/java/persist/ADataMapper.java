package persist;

import java.util.Optional;
import javax.persistence.*;
import main.ChessMain;

abstract class ADataMapper<E> implements IDataMapper<E> {
    private Class<E> c;

    public ADataMapper(Class<E> c){ this.c = c; }

    public Optional<E> find(int id){
        EntityManager em = ChessMain.getEntityManager();
        E res;

        try {
        	res = em.find(c, id);
        } finally {
        	em.close();
        }
        
        Optional<E> opt = Optional.of(res);

        return opt;
    }

    public void update(E e){
    	EntityManager em = ChessMain.getEntityManager();
    	
    	try {
    		em.getTransaction().begin();
        	em.merge(e);
        	em.getTransaction().commit();
    	} finally {
    		em.close();
    	}
    }

    public boolean remove(E e){
    	EntityManager em = ChessMain.getEntityManager();
    	PersistenceUnitUtil unitUtil = ChessMain.emf.getPersistenceUnitUtil();
    	
    	boolean result;
    	
    	try {
    		int entId = (Integer) unitUtil.getIdentifier(e);
    		if (entId != 0) {
    			
    			E obj = em.find(c, entId);
        		
        		em.getTransaction().begin();
        		
        		em.remove(obj);
        		
        		em.getTransaction().commit();
        		
        		result = true;
    			
    		} else {
    			throw new Exception("Entity not in database");
    		}
    		
    		
    	} catch(Exception a) {
    		System.out.println(a.getMessage());
    		result = false;
    	} finally {
    		em.close();
    	}
        return result;
    }

    public int insert(E e){
    	EntityManager em = ChessMain.getEntityManager();
    	PersistenceUnitUtil unitUtil = ChessMain.emf.getPersistenceUnitUtil();
    	
    	try {
    		em.getTransaction().begin();
        	em.persist(e);
        	em.getTransaction().commit();
    		
    	} finally {
    		em.close();
    	}

    	int entId = (Integer) unitUtil.getIdentifier(e);
    	
    	return entId;
    }
}