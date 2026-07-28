package unl.edu.cc.rest.jbrew.domain.Movements;

import jakarta.persistence.*;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Kardex.Kardex;

@Entity
@Table(name = "product_movements")
public class ProductMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int idProductMovement;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Composición con Product
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;
    
    @Column(name = "subtotal", nullable = false)
    private double subtotal;
    
    @ManyToOne
    @JoinColumn(name = "movement_id")
    private Movement movement;

    public ProductMovement() {
        // Constructor sin argumentos requerido por JPA
    }

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }
}
