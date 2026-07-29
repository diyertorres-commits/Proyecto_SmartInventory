package unl.edu.cc.rest.jbrew.domain.Invoice;

import jakarta.persistence.*;
import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;

@Entity
@Table(name = "sale_invoices")
public class SaleInvoice extends Invoice { // Herencia de Invoice

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; // Composición con Customer
    
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "subtotal")
    private double subtotal;
    
    @Column(name = "tax")
    private double tax;
    
    @Column(name = "discount")
    private double discount;

    public SaleInvoice() {
        super();
    }

    public SaleInvoice(int idInvoice, Date invoiceDate, String invoiceNumber, Customer customer,
                       String paymentMethod, Movement movement) { // Asociación con Movement
        super(idInvoice, invoiceDate, invoiceNumber, movement);
        this.customer = customer;
        this.paymentMethod = paymentMethod;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}