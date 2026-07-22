package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CompraBean implements Serializable {
    
    @Inject
    private InventoryService inventoryService;
    
    @Inject
    private PurchaseService purchaseService;
    
    // Objects for RESTOCK operation
    private Product selectedProductForRestock;
    private int restockQuantity;
    private double restockPurchasePrice;
    
    // Object for ACQUIRE NEW PRODUCT operation
    private Product newProduct;
    
    // Common fields
    private Supplier selectedSupplier;
    
    // Purchase history and invoices
    private List<PurchaseService.PurchaseRecord> purchaseHistory;
    private List<PurchaseInvoice> purchaseInvoices;
    
    public CompraBean() {
        this.selectedProductForRestock = null;
        this.restockQuantity = 1;
        this.restockPurchasePrice = 0;
        this.newProduct = new Product();
        this.selectedSupplier = null;
        this.purchaseHistory = List.of();
        this.purchaseInvoices = List.of();
    }
    
    public String processRestockPurchase() {
        if (selectedSupplier == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un proveedor"));
            return null;
        }
        
        if (selectedProductForRestock == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un producto"));
            return null;
        }
        
        PurchaseService.PurchaseResult result = purchaseService.processRestockPurchase(
            selectedProductForRestock, restockQuantity, restockPurchasePrice, selectedSupplier);
        
        if (result.isSuccess()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            refreshPurchaseData();
            clearRestockFields();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", result.getMessage()));
        }
        
        return null;
    }
    
    public String processNewProductPurchase() {
        if (selectedSupplier == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un proveedor"));
            return null;
        }
        
        PurchaseService.PurchaseResult result = purchaseService.processNewProductPurchase(newProduct, selectedSupplier);
        
        if (result.isSuccess()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            refreshPurchaseData();
            clearNewProductFields();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", result.getMessage()));
        }
        
        return null;
    }
    
    private void refreshPurchaseData() {
        this.purchaseHistory = purchaseService.getPurchaseHistory();
        this.purchaseInvoices = purchaseService.getPurchaseInvoices();
    }
    
    private void clearRestockFields() {
        this.selectedProductForRestock = null;
        this.restockQuantity = 1;
        this.restockPurchasePrice = 0;
    }
    
    private void clearNewProductFields() {
        this.newProduct = new Product();
    }
    
    // Getters and Setters
    public Product getSelectedProductForRestock() {
        return selectedProductForRestock;
    }
    
    public void setSelectedProductForRestock(Product selectedProductForRestock) {
        this.selectedProductForRestock = selectedProductForRestock;
    }
    
    public int getRestockQuantity() {
        return restockQuantity;
    }
    
    public int getCantidad() {
        return getRestockQuantity();
    }
    
    public void setRestockQuantity(int restockQuantity) {
        this.restockQuantity = restockQuantity;
    }
    
    public void setCantidad(int restockQuantity) {
        setRestockQuantity(restockQuantity);
    }
    
    public double getRestockPurchasePrice() {
        return restockPurchasePrice;
    }
    
    public void setRestockPurchasePrice(double restockPurchasePrice) {
        this.restockPurchasePrice = restockPurchasePrice;
    }
    
    public Product getNewProduct() {
        return newProduct;
    }
    
    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }
    
    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }
    
    public Supplier getProveedorSeleccionado() {
        return getSelectedSupplier();
    }
    
    public Supplier getProveedor() {
        return getSelectedSupplier();
    }
    
    public int getProductoId() {
        return selectedProductForRestock != null ? selectedProductForRestock.getIdProduct() : 0;
    }
    
    public String getCodigo() {
        return selectedProductForRestock != null ? selectedProductForRestock.getCodigo() : "";
    }
    
    public String getNombre() {
        return selectedProductForRestock != null ? selectedProductForRestock.getName() : "";
    }
    
    public String getDescripcion() {
        return selectedProductForRestock != null ? selectedProductForRestock.getDescription() : "";
    }
    
    public String getCategoria() {
        return selectedProductForRestock != null ? selectedProductForRestock.getCategoria() : "";
    }
    
    public double getPrecioVenta() {
        return selectedProductForRestock != null ? selectedProductForRestock.getSalePrice() : 0;
    }
    
    public double getPrecioCompra() {
        return selectedProductForRestock != null ? selectedProductForRestock.getPurchasePrice() : 0;
    }
    
    public int getStock() {
        return selectedProductForRestock != null ? selectedProductForRestock.getStock() : 0;
    }
    
    public int getStockMinimo() {
        return selectedProductForRestock != null ? selectedProductForRestock.getMinStock() : 0;
    }
    
    public String getImagen() {
        return selectedProductForRestock != null ? selectedProductForRestock.getImagen() : "";
    }
    
    public int getProveedorId() {
        return selectedSupplier != null ? selectedSupplier.getIdSupplier() : 0;
    }
    
    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }
    
    public void setProveedorSeleccionado(Supplier selectedSupplier) {
        setSelectedSupplier(selectedSupplier);
    }
    
    public void setProductoId(int productId) {
        Product product = inventoryService.findProductById(productId).orElse(null);
        setSelectedProductForRestock(product);
    }
    
    public void setCodigo(String codigo) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setCodigo(codigo);
        }
    }
    
    public void setNombre(String nombre) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setName(nombre);
        }
    }
    
    public void setDescripcion(String descripcion) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setDescription(descripcion);
        }
    }
    
    public void setCategoria(String categoria) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setCategoria(categoria);
        }
    }
    
    public void setPrecioVenta(double precio) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setSalePrice(precio);
        }
    }
    
    public void setPrecioCompra(double precio) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setPurchasePrice(precio);
        }
    }
    
    public void setStock(int stock) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setStock(stock);
        }
    }
    
    public void setStockMinimo(int stockMinimo) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setMinStock(stockMinimo);
        }
    }
    
    public void setImagen(String imagen) {
        if (selectedProductForRestock != null) {
            selectedProductForRestock.setImagen(imagen);
        }
    }
    
    public void setProveedorId(int supplierId) {
        Supplier supplier = inventoryService.findSupplierById(supplierId).orElse(null);
        setSelectedSupplier(supplier);
    }
    
    public List<PurchaseService.PurchaseRecord> getPurchaseHistory() {
        if (purchaseHistory.isEmpty()) {
            refreshPurchaseData();
        }
        return purchaseHistory;
    }
    
    public List<PurchaseService.PurchaseRecord> getCompras() {
        return getPurchaseHistory();
    }
    
    public List<PurchaseInvoice> getPurchaseInvoices() {
        if (purchaseInvoices.isEmpty()) {
            refreshPurchaseData();
        }
        return purchaseInvoices;
    }
    
    public double getTotalPurchases() {
        return purchaseService.getTotalPurchases();
    }
    
    public double getTotalCompras() {
        return getTotalPurchases();
    }
    
    public List<Product> getAvailableProducts() {
        return inventoryService.getAllProducts();
    }
    
    public List<Product> getProductosDisponibles() {
        return getAvailableProducts();
    }
    
    public List<Product> getProductos() {
        return getAvailableProducts();
    }
    
    public List<Supplier> getAvailableSuppliers() {
        return inventoryService.getAllSuppliers();
    }
    
    public List<Supplier> getProveedores() {
        return getAvailableSuppliers();
    }
    
    public List<Supplier> getProveedoresDisponibles() {
        return getAvailableSuppliers();
    }
    
    // Compatibility method for ReporteBean
    public List<PurchaseInvoice> getFacturas() {
        return getPurchaseInvoices();
    }
}
