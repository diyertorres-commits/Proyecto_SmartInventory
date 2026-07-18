package unl.edu.cc.rest.jbrew.domain.Reports;

import java.util.Date;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

public class StockAlert {

    private int idAlert;
    private Product product; // Composición con Product
    private int currentStock;
    private int minStock;
    private Date alertDate;
    private boolean isResolved;

    public StockAlert(int idAlert, Product product, int currentStock, int minStock, Date alertDate) { // Asociación con Product
        this.idAlert = idAlert;
        this.product = product;
        this.currentStock = currentStock;
        this.minStock = minStock;
        this.alertDate = alertDate;
        this.isResolved = false;
    }

    public void checkStockLevel() {
        if (currentStock <= minStock) {
            // Alert logic - stock is below minimum
            // The actual alert display is handled by StockAlertView
        }
    }

    public void resolveAlert() {
        this.isResolved = true;
    }

    public void showAlert() {
        // Alert display logic - use presentation layer instead
        // The actual display is handled by StockAlertView.showAlert()
    }

    // Getters and Setters
    public int getIdAlert() {
        return idAlert;
    }

    public void setIdAlert(int idAlert) {
        this.idAlert = idAlert;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMinStock() {
        return minStock;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }
}
