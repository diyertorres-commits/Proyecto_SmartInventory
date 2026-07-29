package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.ProductSearchService;
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

    @Inject
    private ProductSearchService productSearchService;
    
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
        // Copiar el ID de JPA para que merge() funcione correctamente
        this.selectedProduct.setId(product.getId());
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
    
    public void searchProducts() {
        ProductSearchService.ProductSearchCriteria criteria = new ProductSearchService.ProductSearchCriteria();
        criteria.setSearchTerm(searchTerm);
        criteria.setSearchCodigo(searchCodigo);
        criteria.setCategoryFilter(categoryFilter);
        criteria.setStatusFilter(statusFilter);

        filteredProducts = productSearchService.search(criteria);
        initialized = true;
    }
    
    public void clearFilters() {
        searchTerm = "";
        searchCodigo = "";
        categoryFilter = null;
        statusFilter = null;
        filteredProducts = inventoryFacade.getAllProducts();
        initialized = true;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public List<Product> getFilteredProducts() {
        if (!initialized) {
            filteredProducts = inventoryFacade.getAllProducts();
            initialized = true;
        }
        return filteredProducts;
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
    
    public String getSearchCodigo() {
        return searchCodigo;
    }
    
    public void setSearchCodigo(String searchCodigo) {
        this.searchCodigo = searchCodigo;
    }
    
    public String getCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
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
    
    public int getAvailableStockCount() {
        return (int) inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getEstado() == ProductStatus.DISPONIBLE)
                .count();
    }
    
    public int getTotalProducts() {
        return inventoryFacade.getAllProducts().size();
    }

    public double getTotalInventoryValue() {
        return inventoryFacade.getAllProducts().stream()
                .mapToDouble(p -> p.getStock() * p.getPurchasePrice())
                .sum();
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