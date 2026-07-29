package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.dao.ProductDAO;
import unl.edu.cc.rest.jbrew.dao.CategoryDAO;
import unl.edu.cc.rest.jbrew.dao.CustomerDAO;
import unl.edu.cc.rest.jbrew.dao.SupplierDAO;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class InventoryService {

    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());

    @Inject
    private ProductDAO productDAO;

    @Inject
    private CategoryDAO categoryDAO;

    @Inject
    private CustomerDAO customerDAO;

    @Inject
    private SupplierDAO supplierDAO;

    public InventoryService() {
        // Constructor vacío
    }

    // Product operations
    public List<Product> getAllProducts() {
        LOGGER.info("Obteniendo todos los productos desde base de datos");
        return productDAO.findAll();
    }

    public Optional<Product> findProductById(int id) {
        LOGGER.info("Buscando producto por ID desde base de datos: " + id);
        return productDAO.findByIdOptional((long) id);
    }

    public Optional<Product> findProductByName(String name) {
        LOGGER.info("Buscando producto por nombre desde base de datos: " + name);
        return productDAO.findByName(name);
    }

    public List<Product> findProductsByCategory(Category category) {
        if (category == null) {
            return getAllProducts();
        }
        LOGGER.info("Buscando productos por categoría desde base de datos: " + category.getName());
        return productDAO.findByCategory(category.getName());
    }

    public List<Product> findProductsByCategory(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return getAllProducts();
        }
        LOGGER.info("Buscando productos por categoría desde base de datos: " + categoryName);
        return productDAO.findByCategory(categoryName);
    }

    public List<Product> findProductsWithCriticalStock() {
        LOGGER.info("Buscando productos con stock crítico desde base de datos");
        return productDAO.findWithCriticalStock();
    }

    public void saveProduct(Product product) {
        LOGGER.info("Guardando producto en base de datos: " + product.getName());
        productDAO.save(product);
    }

    public void deleteProduct(Product product) {
        LOGGER.info("Eliminando producto de base de datos: " + product.getIdProduct());
        productDAO.delete(product);
    }

    // Category operations
    public List<Category> getAllCategories() {
        LOGGER.info("Obteniendo todas las categorías desde base de datos");
        return categoryDAO.findAll();
    }

    public Optional<Category> findCategoryById(Long id) {
        LOGGER.info("Buscando categoría por ID desde base de datos: " + id);
        return categoryDAO.findByIdOptional(id);
    }

    public Optional<Category> findCategoryByName(String name) {
        LOGGER.info("Buscando categoría por nombre desde base de datos: " + name);
        return categoryDAO.findByName(name);
    }

    public void saveCategory(Category category) {
        LOGGER.info("Guardando categoría en base de datos: " + category.getName());
        categoryDAO.save(category);
    }

    public void updateCategory(Category category) {
        LOGGER.info("Actualizando categoría en base de datos: " + category.getId());
        categoryDAO.save(category);
    }

    public void deleteCategory(Category category) {
        LOGGER.info("Eliminando categoría de base de datos: " + category.getId());
        categoryDAO.delete(category);
    }

    // Customer operations
    public List<Customer> getAllCustomers() {
        LOGGER.info("Obteniendo todos los clientes desde base de datos");
        return customerDAO.findAll();
    }

    public Optional<Customer> findCustomerById(Long id) {
        LOGGER.info("Buscando cliente por ID desde base de datos: " + id);
        return customerDAO.findByIdOptional(id);
    }

    public void saveCustomer(Customer customer) {
        LOGGER.info("Guardando cliente en base de datos: " + customer.getName());
        customerDAO.save(customer);
    }

    public void deleteCustomer(Customer customer) {
        LOGGER.info("Eliminando cliente de base de datos: " + customer.getIdCustomer());
        customerDAO.delete(customer);
    }

    // Supplier operations
    public List<Supplier> getAllSuppliers() {
        LOGGER.info("Obteniendo todos los proveedores desde base de datos");
        return supplierDAO.findAll();
    }

    public Optional<Supplier> findSupplierById(Long id) {
        LOGGER.info("Buscando proveedor por ID desde base de datos: " + id);
        return supplierDAO.findByIdOptional(id);
    }

    public void saveSupplier(Supplier supplier) {
        LOGGER.info("Guardando proveedor en base de datos: " + supplier.getName());
        supplierDAO.save(supplier);
    }

    public void deleteSupplier(Supplier supplier) {
        LOGGER.info("Eliminando proveedor de base de datos: " + supplier.getIdSupplier());
        supplierDAO.delete(supplier);
    }
}