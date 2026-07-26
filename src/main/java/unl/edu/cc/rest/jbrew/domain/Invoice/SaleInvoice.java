package unl.edu.cc.rest.jbrew.domain.Invoice;

import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;

public class SaleInvoice extends Invoice { // Herencia de Invoice

    private Customer customer; // Composición con Customer
    private String paymentMethod;

    // NUEVO: antes solo existía "total" (heredado de Invoice), que además
    // nunca se llenaba porque nada llamaba a generateInvoice(). Se agregan
    // estos campos para que la factura quede trazable: cuánto era el
    // subtotal de productos, cuánto de IVA, y cuánto de descuento se aplicó,
    // sin perder esa información al limpiar el carrito.
    private double subtotal;
    private double tax;
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