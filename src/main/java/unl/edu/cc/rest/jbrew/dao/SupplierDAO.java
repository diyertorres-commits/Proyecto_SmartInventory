package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.List;
import java.util.Optional;

@Stateless
public class SupplierDAO extends BaseDAO<Supplier> {

    @PersistenceContext
    private EntityManager em;

    public SupplierDAO() {
        super(Supplier.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public Optional<Supplier> findByIdSupplier(String idSupplier) {
        return findSingleResult(
            "SELECT s FROM Supplier s WHERE s.idSupplier = :idSupplier",
            "idSupplier", idSupplier);
    }

    public Optional<Supplier> findByName(String name) {
        return findSingleResult(
            "SELECT s FROM Supplier s WHERE s.name = :name",
            "name", name);
    }

    public Optional<Supplier> findByEmail(String email) {
        return findSingleResult(
            "SELECT s FROM Supplier s WHERE s.email = :email",
            "email", email);
    }

    public List<Supplier> findByStatus(String status) {
        return findList(
            "SELECT s FROM Supplier s WHERE s.status = :status",
            "status", status);
    }
}
