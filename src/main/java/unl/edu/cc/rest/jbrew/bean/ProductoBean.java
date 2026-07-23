package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Inventory.ProductStatus;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProductoBean implements Serializable {
    
    private static final Logger LOGGER = Logger.getLogger(ProductoBean.class.getName());
    
    @Inject
    private InventoryFacade inventoryFacade;
    
    private Product selectedProduct;
    private List<Product> filteredProducts;
    
    @NotNull(message = "El término de búsqueda es requerido")
    @Size(min = 3, message = "El término de búsqueda debe tener al menos 3 caracteres")
    private String searchTerm;
    
    private String categoryFilter;
    private String statusFilter;
    
    private int totalStock;
    private int lowStockCount;
    private int availableStockCount;
    
    public ProductoBean() {
        this.selectedProduct = new Product();
        this.filteredProducts = List.of();
        this.searchTerm = "";
        this.categoryFilter = null;
        this.statusFilter = null;
    }
    
    public void prepareNewProduct() {
        this.selectedProduct = new Product();
    }
    
    public void editProduct(Product product) {
        this.selectedProduct = new Product(
            product.getIdProduct(),
            product.getCodigo(),
            product.getName(),
            product.getDescription(),
            product.getCategoria(),
            product.getImagen(),
            product.getSalePrice(),
            product.getPurchasePrice(),
            product.getStock(),
            product.getMinStock()
        );
    }
    
    public void editar(Product product) {
        editProduct(product);
    }
    
    public String saveProduct() {
        try {
            inventoryFacade.saveProduct(selectedProduct);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    selectedProduct.getIdProduct() == 0 ? "Producto creado correctamente" : "Producto actualizado correctamente"));
            prepareNewProduct();
            updateStatistics();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar producto: " + e.getMessage()));
            return null;
        }
    }
    
    public String guardar() {
        return saveProduct();
    }
    
    public void deleteProduct(Product product) {
        try {
            inventoryFacade.deleteProduct(product);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado correctamente"));
            updateStatistics();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar producto: " + e.getMessage()));
        }
    }
    
    public void eliminar(Product product) {
        deleteProduct(product);
    }
    
    public void searchProducts() {
        List<Product> allProducts = inventoryFacade.getAllProducts();
        filteredProducts = allProducts.stream()
                .filter(p -> searchTerm == null || searchTerm.isEmpty() || 
                    p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    p.getCodigo().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
        applyFilters();
    }
    
    public void search() {
        searchProducts();
    }
    
    public void filterProducts() {
        searchProducts();
    }
    
    public void filter() {
        filterProducts();
    }
    
    public void clearFilters() {
        searchTerm = "";
        categoryFilter = null;
        statusFilter = null;
        filteredProducts = inventoryFacade.getAllProducts();
    }
    
    private void applyFilters() {
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(p -> categoryFilter.equals(p.getCategoria()))
                    .toList();
        }
        
        if (statusFilter != null && !statusFilter.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(p -> statusFilter.equals(getStatusText(p.getEstado())))
                    .toList();
        }
    }
    
    private String getStatusText(ProductStatus status) {
        if (status == ProductStatus.DISPONIBLE) return "disponible";
        if (status == ProductStatus.STOCK_BAJO) return "bajo";
        if (status == ProductStatus.AGOTADO) return "agotado";
        return "";
    }
    
    private void updateStatistics() {
        List<Product> allProducts = inventoryFacade.getAllProducts();
        totalStock = allProducts.stream()
                .mapToInt(Product::getStock)
                .sum();
        lowStockCount = (int) allProducts.stream()
                .filter(p -> p.getStock() <= p.getMinStock())
                .count();
        availableStockCount = (int) allProducts.stream()
                .filter(p -> p.getStock() > p.getMinStock())
                .count();
    }
    
    public Product getSelectedProduct() {
        return selectedProduct;
    }
    
    public Product getProducto() {
        return selectedProduct;
    }
    
    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }
    
    public void setProducto(Product selectedProduct) {
        setSelectedProduct(selectedProduct);
    }
    
    public List<Product> getFilteredProducts() {
        if (filteredProducts.isEmpty()) {
            filteredProducts = inventoryFacade.getAllProducts();
            updateStatistics();
        }
        return filteredProducts;
    }
    
    public List<Product> getProductosFiltrados() {
        return getFilteredProducts();
    }
    
    public List<Product> getProductos() {
        return getFilteredProducts();
    }
    
    public void setProductosFiltrados(List<Product> filteredProducts) {
        setFilteredProducts(filteredProducts);
    }
    
    public void setFilteredProducts(List<Product> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public void setTerminoBusqueda(String searchTerm) {
        setSearchTerm(searchTerm);
    }
    
    public String getCategoryFilter() {
        return categoryFilter;
    }
    
    public String getFiltroCategoria() {
        return getCategoryFilter();
    }
    
    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }
    
    public String getStatusFilter() {
        return statusFilter;
    }
    
    public String getFiltroEstado() {
        return getStatusFilter();
    }
    
    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }
    
    public void setFiltroEstado(String statusFilter) {
        setStatusFilter(statusFilter);
    }
    
    public int getTotalStock() {
        return totalStock;
    }
    
    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }
    
    public int getLowStockCount() {
        return lowStockCount;
    }
    
    public int getStockBajo() {
        return getLowStockCount();
    }
    
    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }
    
    public int getAvailableStockCount() {
        return availableStockCount;
    }
    
    public int getStockDisponible() {
        return getAvailableStockCount();
    }
    
    public void setAvailableStockCount(int availableStockCount) {
        this.availableStockCount = availableStockCount;
    }
    
    public int getTotalProducts() {
        return inventoryFacade.getAllProducts().size();
    }
    
    public int getTotalProductos() {
        return getTotalProducts();
    }
    
    public double getTotalInventoryValue() {
        return inventoryFacade.getAllProducts().stream()
                .mapToDouble(p -> p.getStock() * p.getPurchasePrice())
                .sum();
    }
    
    public double getValorTotalInventario() {
        return getTotalInventoryValue();
    }
}
