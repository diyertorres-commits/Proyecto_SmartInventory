package unl.edu.cc.rest.jbrew.domain.Movements;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Kardex.Kardex;

public class Movement {
    private int idMovement;
    private MovementType movementType; // Asociación con MovementType (enum)
    private MovementStatus status; // Asociación con MovementStatus (enum)
    private Date date;
    private String description;
    private List<ProductMovement> productMovementList; // Composición con ProductMovement
    private double total;

    public Movement() {
        productMovementList = new ArrayList<>();
        this.status = MovementStatus.PENDING;
    }

    public Movement(int idMovement, MovementType movementType, Date date, String description) { // Asociación con MovementType
        this();
        this.idMovement = idMovement;
        this.movementType = movementType;
        this.date = date;
        this.description = description;
    }

    public void addProductMovement(Product product, int quantity, double unitPrice) { // Asociación con Product
        ProductMovement productMovement = new ProductMovement(productMovementList.size() + 1, product, quantity, unitPrice); // Composición con ProductMovement
        productMovementList.add(productMovement);
    }


    private void processStockChanges() {
        for (ProductMovement productMovement : productMovementList) {
            if (movementType == MovementType.ENTRY) {
                productMovement.getProduct().modifyStock(productMovement.getQuantity());
            } else {
                productMovement.getProduct().modifyStock(-productMovement.getQuantity());
            }
        }
    }

    public void processMovement() {
        processStockChanges();
        registerKardexEntries();
        this.status = MovementStatus.CONFIRMED;
        calculateTotal();
    }

    private void registerKardexEntries() {
        for (ProductMovement productMovement : productMovementList) {
            Product product = productMovement.getProduct();
            Kardex kardex = new Kardex(
                product.getKardexList().size() + 1,
                product,
                new Date(),
                movementType,
                productMovement.getQuantity(),
                product.getStock(),
                description
            );
            product.addKardex(kardex);
        }
    }


    public void cancelMovement() {
        this.status = MovementStatus.CANCELLED;
    }

    public double calculateTotal() {
        total = 0;
        for (ProductMovement productMovement : productMovementList) {
            total += productMovement.getSubtotal();
        }
        return total;
    }

    // Getters and Setters
    public int getIdMovement() {
        return idMovement;
    }

    public void setIdMovement(int idMovement) {
        this.idMovement = idMovement;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public MovementStatus getStatus() {
        return status;
    }

    public void setStatus(MovementStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductMovement> getProductMovementList() {
        return productMovementList;
    }

    public void setProductMovementList(List<ProductMovement> productMovementList) {
        this.productMovementList = productMovementList;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
