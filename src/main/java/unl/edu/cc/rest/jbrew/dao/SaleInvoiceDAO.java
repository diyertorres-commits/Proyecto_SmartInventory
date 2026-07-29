package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class SaleInvoiceDAO extends BaseDAO<SaleInvoice> {

    @PersistenceContext
    private EntityManager em;

    public SaleInvoiceDAO() {
        super(SaleInvoice.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public Optional<SaleInvoice> findByInvoiceNumber(String invoiceNumber) {
        return findSingleResult(
            "SELECT s FROM SaleInvoice s WHERE s.invoiceNumber = :invoiceNumber",
            "invoiceNumber", invoiceNumber);
    }

    public List<SaleInvoice> findByDateRange(Date startDate, Date endDate) {
        return em.createQuery(
            "SELECT s FROM SaleInvoice s WHERE s.invoiceDate BETWEEN :startDate AND :endDate", SaleInvoice.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }

    public List<SaleInvoice> findByCustomer(Long customerId) {
        return em.createQuery(
            "SELECT s FROM SaleInvoice s WHERE s.customer.id = :customerId", SaleInvoice.class)
            .setParameter("customerId", customerId)
            .getResultList();
    }

    public List<SaleInvoice> findByPaymentMethod(String paymentMethod) {
        return findList(
            "SELECT s FROM SaleInvoice s WHERE s.paymentMethod = :paymentMethod",
            "paymentMethod", paymentMethod);
    }

    public List<SaleInvoice> findByStatus(String status) {
        return findList(
            "SELECT s FROM SaleInvoice s WHERE s.status = :status",
            "status", status);
    }

    public long countByDateRange(Date startDate, Date endDate) {
        return em.createQuery(
            "SELECT COUNT(s) FROM SaleInvoice s WHERE s.invoiceDate BETWEEN :startDate AND :endDate", Long.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getSingleResult();
    }
}
