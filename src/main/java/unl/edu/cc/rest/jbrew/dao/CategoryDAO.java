package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;

import java.util.List;
import java.util.Optional;

@Stateless
public class CategoryDAO extends BaseDAO<Category> {

    @PersistenceContext
    private EntityManager em;

    public CategoryDAO() {
        super(Category.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public Optional<Category> findByName(String name) {
        return findSingleResult(
            "SELECT c FROM Category c WHERE c.name = :name",
            "name", name);
    }

    public List<Category> findByStatus(String status) {
        return findList(
            "SELECT c FROM Category c WHERE c.status = :status",
            "status", status);
    }

    public boolean hasProducts(Long categoryId) {
        Long count = em.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId", Long.class)
            .setParameter("categoryId", categoryId)
            .getSingleResult();
        return count > 0;
    }
}
