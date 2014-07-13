package tuwien.big.formel0.entities;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @author petra
 */
@ManagedBean(name = "dataService", eager = true)
@ApplicationScoped
public class DataService {

    private static final Logger logger = Logger.getLogger(DataService.class.getName());
    private static EntityManagerFactory emf = null;
    private EntityManager entityManager;

    /**
     * open DataService and also start a transaction
     */
    public DataService() {
    }

    @PostConstruct
    private void initialize() {
        logger.log(Level.INFO, "{0} initialized.", this.getClass().getName());
        try {
            emf = Persistence.createEntityManagerFactory("lab4");

        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to setup persistence unit!", t);
        }
    }

    public <T extends BaseEntity> T createNew(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends BaseEntity> void persist(T entity) {
        entityManager = getActiveEm();
        entityManager.persist(entity);
        closeEm();
    }

    public <T extends BaseEntity> List<T> findAll(Class<T> clazz) {
        entityManager = getActiveEm();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        List<T> resultlist = entityManager.createQuery(query.select(query.from(clazz))).getResultList();
        closeEm();
        return resultlist;
    }

    public <T extends BaseEntity> T findById(Class<T> clazz, long id) {
        entityManager = getActiveEm();
        T result = entityManager.find(clazz, id);
        closeEm();
        return result;
    }

    public <T extends BaseEntity> void delete(T entity) {
        entityManager = getActiveEm();
        entityManager.remove(entity);
        closeEm();
    }

    public <T extends BaseEntity> T merge(T entity) {
        entityManager = getActiveEm();
        T result = entityManager.merge(entity);
        closeEm();
        return result;
    }

    public <T extends BaseEntity> boolean contains(T entity) {
        entityManager = getActiveEm();
        boolean result = entityManager.contains(entity);
        closeEm();
        return result;
    }

    <T extends BaseEntity> List<T> executeNamedQuery(Class<T> clazz, String namedQuery, ParameterBinding... params) {
        entityManager = getActiveEm();
        TypedQuery<T> typedQuery = entityManager.createNamedQuery(namedQuery, clazz);
        for (ParameterBinding p : params) {
            typedQuery.setParameter(p.getParameterName(), p.getParameterValue());
        }
        List<T> resultlist = null;
        try {
            resultlist = typedQuery.getResultList();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "No result for query {0}", typedQuery);
        }
        closeEm();
        return resultlist;
    }

    <T> T executeScalarNamedQuery(Class<T> clazz, String namedQuery, ParameterBinding... params) {
        entityManager = getActiveEm();
        TypedQuery<T> typedQuery = entityManager.createNamedQuery(namedQuery, clazz);
        for (ParameterBinding p : params) {
            typedQuery.setParameter(p.getParameterName(), p.getParameterValue());
        }
        T result = null;
        try {
            result = typedQuery.getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "No result for query {0}", typedQuery);
        }
        closeEm();
        return result;
    }

    @PreDestroy
    public void cleanup() {
        logger.log(Level.INFO, "{0} destroyed.", this.getClass().getName());

        if (this.entityManager != null && this.entityManager.isOpen()) {
            this.entityManager.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Get an instance of {@link EntityManager} with active transaction
     */
    private EntityManager getActiveEm() {
        entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();

        if (entityManager == null) {
            throw new IllegalStateException("No transaction was active!");
        }

        return entityManager;
    }

    /**
     * Commit or rollback the active transaction and close the entity manager.
     */
    private void closeEm() {
        if (entityManager == null) {
            return;
        }

        try {
            if (entityManager.getTransaction().isActive()) {

                if (entityManager.getTransaction().getRollbackOnly()) {
                    entityManager.getTransaction().rollback();
                } else {
                    entityManager.getTransaction().commit();
                }
            }
        } finally {
            entityManager.close();
            entityManager = null;
        }
    }
}
