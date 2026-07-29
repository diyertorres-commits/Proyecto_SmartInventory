package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.util.List;
import java.util.Optional;

@Stateless
public class ProductDAO extends BaseDAO<Product> {

    @PersistenceContext
    private EntityManager em;

    public ProductDAO() {
        super(Product.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public Optional<Product> findByCodigo(String codigo) {
        return findSingleResult(
            "SELECT p FROM Product p WHERE p.codigo = :codigo",
            "codigo", codigo);
    }

    public Optional<Product> findByName(String name) {
        return findSingleResult(
            "SELECT p FROM Product p WHERE p.name = :name",
            "name", name);
    }

    public List<Product> findByCategory(String categoryName) {
        return findList(
            "SELECT p FROM Product p WHERE p.category.name = :categoryName",
            "categoryName", categoryName);
    }

    public List<Product> findWithCriticalStock() {
        return em.createQuery(
            "SELECT p FROM Product p WHERE p.stock <= p.minStock", Product.class)
            .getResultList();
    }

    public List<Product> findOutOfStock() {
        return em.createQuery(
            "SELECT p FROM Product p WHERE p.stock = 0", Product.class)
            .getResultList();
    }

    public List<Product> findByStatus(String status) {
        return findList(
            "SELECT p FROM Product p WHERE p.estado = :status",
            "status", status);
    }

    public long countByCategory(String categoryName) {
        return em.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category.name = :categoryName", Long.class)
            .setParameter("categoryName", categoryName)
            .getSingleResult();
    }
}
