package unl.edu.cc.rest.jbrew.domain.Invoice;

import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;

public class PurchaseInvoice extends Invoice { // Herencia de Invoice
    private Supplier supplier; // Composición con Supplier
    private String purchaseOrderNumber;

    public PurchaseInvoice() {
        super();
    }

    public PurchaseInvoice(int idInvoice, Date invoiceDate, String invoiceNumber, Supplier supplier, String purchaseOrderNumber, Movement movement) { // Asociación con Movement
        super(idInvoice, invoiceDate, invoiceNumber, movement);
        this.supplier = supplier;
        this.purchaseOrderNumber = purchaseOrderNumber;
    }


    // Getters and Setters
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }
}
