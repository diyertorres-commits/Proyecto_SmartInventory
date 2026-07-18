package unl.edu.cc.rest.jbrew.domain.Invoice;

import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;

public abstract class Invoice {
    private int idInvoice;
    private Date invoiceDate;
    private double total;
    private String invoiceNumber;
    private Movement movement; // Composición con Movement

    public Invoice() {
    }

    public Invoice(int idInvoice, Date invoiceDate, String invoiceNumber, Movement movement) { // Asociación con Movement
        this.idInvoice = idInvoice;
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
        this.movement = movement;
    }

    public void calculateTotal() {
        if (getMovement() != null) {
            setTotal(getMovement().calculateTotal());
        }
    }

    public void generateInvoice() {
        calculateTotal();
    }

    // Getters and Setters
    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }
}
