package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class PurchaseInvoiceDAO extends BaseDAO<PurchaseInvoice> {

    @PersistenceContext
    private EntityManager em;

    public PurchaseInvoiceDAO() {
        super(PurchaseInvoice.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public Optional<PurchaseInvoice> findByInvoiceNumber(String invoiceNumber) {
        return findSingleResult(
            "SELECT p FROM PurchaseInvoice p WHERE p.invoiceNumber = :invoiceNumber",
            "invoiceNumber", invoiceNumber);
    }

    public List<PurchaseInvoice> findByDateRange(Date startDate, Date endDate) {
        return em.createQuery(
            "SELECT p FROM PurchaseInvoice p WHERE p.invoiceDate BETWEEN :startDate AND :endDate", PurchaseInvoice.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }

    public List<PurchaseInvoice> findBySupplier(Long supplierId) {
        return em.createQuery(
            "SELECT p FROM PurchaseInvoice p WHERE p.supplier.id = :supplierId", PurchaseInvoice.class)
            .setParameter("supplierId", supplierId)
            .getResultList();
    }

    public List<PurchaseInvoice> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return findList(
            "SELECT p FROM PurchaseInvoice p WHERE p.purchaseOrderNumber = :purchaseOrderNumber",
            "purchaseOrderNumber", purchaseOrderNumber);
    }

    public List<PurchaseInvoice> findByStatus(String status) {
        return findList(
            "SELECT p FROM PurchaseInvoice p WHERE p.status = :status",
            "status", status);
    }

    public long countByDateRange(Date startDate, Date endDate) {
        return em.createQuery(
            "SELECT COUNT(p) FROM PurchaseInvoice p WHERE p.invoiceDate BETWEEN :startDate AND :endDate", Long.class)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getSingleResult();
    }
}
