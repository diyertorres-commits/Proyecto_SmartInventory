package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.People.Customer;

import java.util.List;
import java.util.Optional;

@Stateless
public class CustomerDAO extends BaseDAO<Customer> {

    @PersistenceContext
    private EntityManager em;

    public CustomerDAO() {
        super(Customer.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public Optional<Customer> findByIdCustomer(String idCustomer) {
        return findSingleResult(
            "SELECT c FROM Customer c WHERE c.idCustomer = :idCustomer",
            "idCustomer", idCustomer);
    }

    public Optional<Customer> findByName(String name) {
        return findSingleResult(
            "SELECT c FROM Customer c WHERE c.name = :name",
            "name", name);
    }

    public Optional<Customer> findByEmail(String email) {
        return findSingleResult(
            "SELECT c FROM Customer c WHERE c.email = :email",
            "email", email);
    }

    public List<Customer> findByStatus(String status) {
        return findList(
            "SELECT c FROM Customer c WHERE c.status = :status",
            "status", status);
    }
}
