package unl.edu.cc.rest.jbrew.domain.Kardex;

import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Movements.MovementType;

public class Kardex {
    private int idKardex;
    private Product product; // Composición con Product
    private Date date;
    private MovementType movementType; // Asociación con MovementType (enum)
    private int quantity;
    private int balance;
    private String description;

    public Kardex(int idKardex, Product product, Date date, MovementType movementType, int quantity, int balance, String description) { // Asociación con Product y MovementType
        this.idKardex = idKardex;
        this.product = product;
        this.date = date;
        this.movementType = movementType;
        this.quantity = quantity;
        this.balance = balance;
        this.description = description;
    }

    public void registerEntry(int quantity) {
        this.quantity = quantity;
        this.movementType = MovementType.ENTRY;
        this.balance += quantity;
        this.date = new Date();
    }

    public void registerExit(int quantity) {
        this.quantity = quantity;
        this.movementType = MovementType.EXIT;
        this.balance -= quantity;
        this.date = new Date();
    }

    public void showKardexEntry() {
        // Kardex display logic - use presentation layer instead
        // The actual display is handled by KardexView.showKardexEntry()
    }

    // Getters and Setters
    public int getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(int idKardex) {
        this.idKardex = idKardex;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
