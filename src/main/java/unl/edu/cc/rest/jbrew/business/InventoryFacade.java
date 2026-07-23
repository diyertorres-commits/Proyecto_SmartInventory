package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class InventoryFacade {
    
    private static final Logger LOGGER = Logger.getLogger(InventoryFacade.class.getName());
    
    @Inject
    private InventoryService inventoryService;
    
    public List<Product> getAllProducts() {
        LOGGER.info("Obteniendo todos los productos a través de facade");
        return inventoryService.getAllProducts();
    }
    
    public Optional<Product> findProductById(int id) {
        LOGGER.info("Buscando producto por ID a través de facade: " + id);
        return inventoryService.findProductById(id);
    }
    
    public Optional<Customer> findCustomerById(int id) {
        LOGGER.info("Buscando cliente por ID a través de facade: " + id);
        return inventoryService.findCustomerById(id);
    }
    
    public Optional<Supplier> findSupplierById(int id) {
        LOGGER.info("Buscando proveedor por ID a través de facade: " + id);
        return inventoryService.findSupplierById(id);
    }
    
    public Optional<Product> findProductByName(String name) {
        LOGGER.info("Buscando producto por nombre a través de facade: " + name);
        return inventoryService.findProductByName(name);
    }
    
    public List<Category> getAllCategories() {
        LOGGER.info("Obteniendo todas las categorías a través de facade");
        return inventoryService.getAllCategories();
    }
    
    public List<Customer> getAllCustomers() {
        LOGGER.info("Obteniendo todos los clientes a través de facade");
        return inventoryService.getAllCustomers();
    }
    
    public List<Supplier> getAllSuppliers() {
        LOGGER.info("Obteniendo todos los proveedores a través de facade");
        return inventoryService.getAllSuppliers();
    }
    
    public List<Product> findProductsByCategory(String categoryName) {
        LOGGER.info("Buscando productos por categoría a través de facade: " + categoryName);
        return inventoryService.findProductsByCategory(categoryName);
    }
    
    public void saveProduct(Product product) {
        LOGGER.info("Guardando producto a través de facade: " + product.getName());
        inventoryService.saveProduct(product);
    }
    
    public void deleteProduct(Product product) {
        LOGGER.info("Eliminando producto a través de facade: " + product.getIdProduct());
        inventoryService.deleteProduct(product);
    }
    
    public void saveCustomer(Customer customer) {
        LOGGER.info("Guardando cliente a través de facade: " + customer.getIdCustomer());
        inventoryService.saveCustomer(customer);
    }
    
    public void deleteCustomer(Customer customer) {
        LOGGER.info("Eliminando cliente a través de facade: " + customer.getIdCustomer());
        inventoryService.deleteCustomer(customer);
    }
    
    public void saveSupplier(Supplier supplier) {
        LOGGER.info("Guardando proveedor a través de facade: " + supplier.getIdSupplier());
        inventoryService.saveSupplier(supplier);
    }
    
    public void deleteSupplier(Supplier supplier) {
        LOGGER.info("Eliminando proveedor a través de facade: " + supplier.getIdSupplier());
        inventoryService.deleteSupplier(supplier);
    }
}
