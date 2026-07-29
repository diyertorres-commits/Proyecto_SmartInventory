package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Inventory.ProductStatus;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProductSearchService {

    @Inject
    private InventoryService inventoryService;

    public List<Product> search(ProductSearchCriteria criteria) {
        List<Product> allProducts = inventoryService.getAllProducts();

        return allProducts.stream()
                .filter(p -> matchesName(p, criteria.getSearchTerm()))
                .filter(p -> matchesCode(p, criteria.getSearchCodigo()))
                .filter(p -> matchesCategory(p, criteria.getCategoryFilter()))
                .filter(p -> matchesStatus(p, criteria.getStatusFilter()))
                .collect(Collectors.toList());
    }

    private boolean matchesName(Product product, String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return true;
        }
        return product.getName().toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean matchesCode(Product product, String searchCodigo) {
        if (searchCodigo == null || searchCodigo.isEmpty()) {
            return true;
        }
        return product.getCodigo().toLowerCase().contains(searchCodigo.toLowerCase());
    }

    private boolean matchesCategory(Product product, String categoryFilter) {
        if (categoryFilter == null || categoryFilter.isEmpty()) {
            return true;
        }
        if (product.getCategory() == null) {
            return false;
        }
        return product.getCategory().getName().equalsIgnoreCase(categoryFilter);
    }

    private boolean matchesStatus(Product product, String statusFilter) {
        if (statusFilter == null || statusFilter.isEmpty()) {
            return true;
        }
        String statusText = getStatusText(product.getEstado());
        return statusText.equalsIgnoreCase(statusFilter);
    }

    private String getStatusText(ProductStatus status) {
        if (status == ProductStatus.DISPONIBLE) return "disponible";
        if (status == ProductStatus.STOCK_BAJO) return "bajo";
        if (status == ProductStatus.AGOTADO) return "agotado";
        return "";
    }

    public static class ProductSearchCriteria {
        private String searchTerm;
        private String searchCodigo;
        private String categoryFilter;
        private String statusFilter;

        public ProductSearchCriteria() {
            this.searchTerm = "";
            this.searchCodigo = "";
            this.categoryFilter = null;
            this.statusFilter = null;
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
    }
}
