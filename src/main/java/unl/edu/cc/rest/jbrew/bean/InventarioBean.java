package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.util.List;

@Named
@ApplicationScoped
public class InventarioBean {
    
    @Inject
    private InventoryService inventoryService;
    
    public InventarioBean() {
        // Constructor vacío, la inicialización se maneja en InventoryService
    }
    
    // Getters delegados a InventoryService
    public List<Product> getProductos() {
        return inventoryService.getAllProducts();
    }
    
    public List<Category> getCategorias() {
        return inventoryService.getAllCategories();
    }
    
    public List<Customer> getClientes() {
        return inventoryService.getAllCustomers();
    }
    
    public List<Supplier> getProveedores() {
        return inventoryService.getAllSuppliers();
    }
    
    // Métodos auxiliares delegados a InventoryService
    public List<Product> getProductosPorCategoria(Category categoria) {
        return inventoryService.findProductsByCategory(categoria);
    }
    
    public List<Product> getProductosPorCategoria(String nombreCategoria) {
        return inventoryService.findProductsByCategory(nombreCategoria);
    }
    
    public List<Product> getProductosStockCritico() {
        return inventoryService.findProductsWithCriticalStock();
    }
    
    public Product buscarProductoPorId(int id) {
        return inventoryService.findProductById(id).orElse(null);
    }

    public Product buscarProductoPorNombre(String nombre) {
        return inventoryService.findProductByName(nombre).orElse(null);
    }

    public Customer buscarClientePorId(int id) {
        return inventoryService.findCustomerById(id).orElse(null);
    }
    
    public Supplier buscarProveedorPorId(int id) {
        return inventoryService.findSupplierById(id).orElse(null);
    }
}
