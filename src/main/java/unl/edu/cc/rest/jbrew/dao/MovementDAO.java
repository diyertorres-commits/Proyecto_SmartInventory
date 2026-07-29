package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;

import java.util.List;

@Stateless
public class MovementDAO extends BaseDAO<Movement> {

    @PersistenceContext
    private EntityManager em;

    public MovementDAO() {
        super(Movement.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public List<Movement> findAllOrderByDateDesc() {
        return em.createQuery(
            "SELECT m FROM Movement m ORDER BY m.movementDate DESC", Movement.class)
            .getResultList();
    }

    public List<Movement> findByType(String movementType) {
        return findList(
            "SELECT m FROM Movement m WHERE m.movementType = :movementType",
            "movementType", movementType);
    }

    public Movement findLastMovement() {
        List<Movement> movements = em.createQuery(
            "SELECT m FROM Movement m ORDER BY m.id DESC", Movement.class)
            .setMaxResults(1)
            .getResultList();
        return movements.isEmpty() ? null : movements.get(0);
    }
}
