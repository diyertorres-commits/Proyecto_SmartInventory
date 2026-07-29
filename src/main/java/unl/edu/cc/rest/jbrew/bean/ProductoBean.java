package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
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
    
    private String searchTerm;
    
    private String searchCodigo;
    
    private String categoryFilter;
    private String statusFilter;
    
    private boolean initialized = false;
    
    public ProductoBean() {
        this.selectedProduct = new Product();
        this.filteredProducts = List.of();
        this.searchTerm = "";
        this.searchCodigo = "";
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
            product.getCategory(),
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
            if (selectedProduct.getIdProduct() != 0) {
                // Es edición: recuperar el stock real actual desde el backend
                // para evitar que se sobrescriba con un valor manipulado en el cliente.
                // El stock solo debe modificarse desde la sección de Ajustes.
                inventoryFacade.findProductById(selectedProduct.getIdProduct())
                        .ifPresent(existente -> selectedProduct.setStock(existente.getStock()));
            }

            inventoryFacade.saveProduct(selectedProduct);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    selectedProduct.getIdProduct() == 0 ? "Producto creado correctamente" : "Producto actualizado correctamente"));
            prepareNewProduct();
            searchProducts();
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
            searchProducts();
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
        
        boolean hasNameFilter = searchTerm != null && !searchTerm.isEmpty();
        boolean hasCodeFilter = searchCodigo != null && !searchCodigo.isEmpty();
        
        filteredProducts = allProducts.stream()
                .filter(p -> {
                    if (!hasNameFilter && !hasCodeFilter) {
                        return true; // Sin filtros, mostrar todos
                    }
                    
                    boolean matchesName = !hasNameFilter || 
                        p.getName().toLowerCase().contains(searchTerm.toLowerCase());
                    boolean matchesCode = !hasCodeFilter || 
                        p.getCodigo().toLowerCase().contains(searchCodigo.toLowerCase());
                    
                    return matchesName && matchesCode;
                })
                .toList();
        applyFilters();
        initialized = true;
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
        searchCodigo = "";
        categoryFilter = null;
        statusFilter = null;
        filteredProducts = inventoryFacade.getAllProducts();
        initialized = true;
    }
    
    private void applyFilters() {
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(p -> categoryFilter.equals(p.getCategory() != null ? p.getCategory().getName() : ""))
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
        if (!initialized) {
            filteredProducts = inventoryFacade.getAllProducts();
            initialized = true;
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
    
    public String getSearchCodigo() {
        return searchCodigo;
    }
    
    public void setSearchCodigo(String searchCodigo) {
        this.searchCodigo = searchCodigo;
    }
    
    public String getCategoryFilter() {
        return categoryFilter;
    }
    
    public String getFiltroCategoria() {
        return getCategoryFilter();
    }

    public void setFiltroCategoria(String categoryFilter) {
        setCategoryFilter(categoryFilter);
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
    
    // ===== KPIs calculados en vivo, siempre consistentes con p.getEstado() =====
    
    public int getTotalStock() {
        return inventoryFacade.getAllProducts().stream()
                .mapToInt(Product::getStock)
                .sum();
    }
    
    public int getLowStockCount() {
        return (int) inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getEstado() == ProductStatus.STOCK_BAJO)
                .count();
    }
    
    public int getStockBajo() {
        return getLowStockCount();
    }
    
    public int getAvailableStockCount() {
        return (int) inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getEstado() == ProductStatus.DISPONIBLE)
                .count();
    }
    
    public int getStockDisponible() {
        return getAvailableStockCount();
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
    
    public String getCategoryName() {
        if (selectedProduct != null && selectedProduct.getCategory() != null) {
            return selectedProduct.getCategory().getName();
        }
        return null;
    }
    
    public void setCategoryName(String categoryName) {
        if (selectedProduct != null && categoryName != null) {
            Category category = inventoryFacade.findCategoryByName(categoryName).orElse(null);
            selectedProduct.setCategory(category);
        }
    }
}