package unl.edu.cc.rest.jbrew.domain.Kardex;

import jakarta.persistence.*;
import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Movements.MovementType;

@Entity
@Table(name = "kardex")
public class Kardex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_kardex")
    private int idKardex;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Composición con Product
    
    @Column(name = "date", nullable = false)
    private Date date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType; // Asociación con MovementType (enum)
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "balance", nullable = false)
    private int balance;
    
    @Column(name = "description")
    private String description;

    public Kardex() {
        // Constructor sin argumentos requerido por JPA
    }

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
