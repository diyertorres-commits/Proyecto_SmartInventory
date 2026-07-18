package unl.edu.cc.rest.jbrew.domain.Movements;

import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Kardex.Kardex;

public class ProductMovement {

    private int idProductMovement;
    private Product product; // Composición con Product
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public ProductMovement(int idProductMovement, Product product, int quantity, double unitPrice) { // Asociación con Product
        this.idProductMovement = idProductMovement;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    public double calculateSubtotal() {
        return quantity * unitPrice;
    }

    public void updateKardex(Kardex kardex, MovementType movementType) { // Asociación con Kardex y MovementType
        if (movementType == MovementType.ENTRY) {
            kardex.registerEntry(quantity);
        } else {
            kardex.registerExit(quantity);
        }
    }

    // Getters y Setters
    public int getIdProductMovement() {
        return idProductMovement;
    }

    public void setIdProductMovement(int idProductMovement) {
        this.idProductMovement = idProductMovement;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
