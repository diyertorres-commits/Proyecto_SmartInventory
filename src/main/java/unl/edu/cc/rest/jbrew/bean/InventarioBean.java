package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.util.List;

@Named
@ViewScoped
public class InventarioBean implements java.io.Serializable {
    
    @Inject
    private InventoryFacade inventoryFacade;
    
    public InventarioBean() {
        // Constructor vacío, la inicialización se maneja en InventoryService
    }
    
    // Getters delegados a InventoryFacade
    public List<Product> getProductos() {
        return inventoryFacade.getAllProducts();
    }
    
    public List<Category> getCategorias() {
        return inventoryFacade.getAllCategories();
    }
    
    public List<Customer> getClientes() {
        return inventoryFacade.getAllCustomers();
    }
    
    public List<Supplier> getProveedores() {
        return inventoryFacade.getAllSuppliers();
    }
    
    // Métodos auxiliares delegados a InventoryFacade
    public List<Product> getProductosPorCategoria(Category categoria) {
        return inventoryFacade.getAllProducts().stream()
                .filter(p -> categoria.getName().equals(p.getCategoria()))
                .toList();
    }
    
    public List<Product> getProductosPorCategoria(String nombreCategoria) {
        return inventoryFacade.findProductsByCategory(nombreCategoria);
    }
    
    public List<Product> getProductosStockCritico() {
        return inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getStock() <= p.getMinStock())
                .toList();
    }
    
    public Product buscarProductoPorId(int id) {
        return inventoryFacade.findProductById(id).orElse(null);
    }

    public Product buscarProductoPorNombre(String nombre) {
        return inventoryFacade.findProductByName(nombre).orElse(null);
    }

    public Customer buscarClientePorId(int id) {
        return inventoryFacade.getAllCustomers().stream()
                .filter(c -> c.getIdCustomer() == id)
                .findFirst()
                .orElse(null);
    }
    
    public Supplier buscarProveedorPorId(int id) {
        return inventoryFacade.getAllSuppliers().stream()
                .filter(s -> s.getIdSupplier() == id)
                .findFirst()
                .orElse(null);
    }
}
