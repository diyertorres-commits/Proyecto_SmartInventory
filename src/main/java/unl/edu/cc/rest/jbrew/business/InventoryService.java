package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.business.service.CrudGenericService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class InventoryService {

    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());

    @Inject
    private CrudGenericService crudGenericService;

    public InventoryService() {
        // Constructor vacío
    }

    // Product operations
    public List<Product> getAllProducts() {
        LOGGER.info("Obteniendo todos los productos desde base de datos");
        return crudGenericService.findWithQuery("SELECT p FROM Product p");
    }

    public Optional<Product> findProductById(int id) {
        LOGGER.info("Buscando producto por ID desde base de datos: " + id);
        Product product = crudGenericService.find(Product.class, (long) id);
        return Optional.ofNullable(product);
    }

    public Optional<Product> findProductByName(String name) {
        LOGGER.info("Buscando producto por nombre desde base de datos: " + name);
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        List<Product> products = crudGenericService.findWithQuery(
            "SELECT p FROM Product p WHERE p.name = :name", params
        );
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }

    public List<Product> findProductsByCategory(Category category) {
        if (category == null) {
            return getAllProducts();
        }
        LOGGER.info("Buscando productos por categoría desde base de datos: " + category.getName());
        Map<String, Object> params = new HashMap<>();
        params.put("categoryName", category.getName());
        return crudGenericService.findWithQuery(
            "SELECT p FROM Product p WHERE p.category.name = :categoryName", params
        );
    }

    public List<Product> findProductsByCategory(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return getAllProducts();
        }
        LOGGER.info("Buscando productos por categoría desde base de datos: " + categoryName);
        Map<String, Object> params = new HashMap<>();
        params.put("categoryName", categoryName);
        return crudGenericService.findWithQuery(
            "SELECT p FROM Product p WHERE p.category.name = :categoryName", params
        );
    }

    public List<Product> findProductsWithCriticalStock() {
        LOGGER.info("Buscando productos con stock crítico desde base de datos");
        return crudGenericService.findWithQuery(
            "SELECT p FROM Product p WHERE p.stock <= p.minStock"
        );
    }

    public void saveProduct(Product product) {
        LOGGER.info("Guardando producto en base de datos: " + product.getName());
        if (product.getId() == null) {
            crudGenericService.create(product);
        } else {
            crudGenericService.update(product);
        }
    }

    public void deleteProduct(Product product) {
        LOGGER.info("Eliminando producto de base de datos: " + product.getIdProduct());
        crudGenericService.delete(Product.class, product.getId());
    }

    // Category operations
    public List<Category> getAllCategories() {
        LOGGER.info("Obteniendo todas las categorías desde base de datos");
        return crudGenericService.findWithQuery("SELECT c FROM Category c");
    }

    public Optional<Category> findCategoryById(Long id) {
        LOGGER.info("Buscando categoría por ID desde base de datos: " + id);
        Category category = crudGenericService.find(Category.class, id);
        return Optional.ofNullable(category);
    }

    public Optional<Category> findCategoryByName(String name) {
        LOGGER.info("Buscando categoría por nombre desde base de datos: " + name);
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        List<Category> categories = crudGenericService.findWithQuery(
            "SELECT c FROM Category c WHERE c.name = :name", params
        );
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    public void saveCategory(Category category) {
        LOGGER.info("Guardando categoría en base de datos: " + category.getName());
        if (category.getId() == null) {
            crudGenericService.create(category);
        } else {
            crudGenericService.update(category);
        }
    }

    public void updateCategory(Category category) {
        LOGGER.info("Actualizando categoría en base de datos: " + category.getId());
        crudGenericService.update(category);
    }

    public void deleteCategory(Category category) {
        LOGGER.info("Eliminando categoría de base de datos: " + category.getId());
        crudGenericService.delete(Category.class, category.getId());
    }

    // Customer operations
    public List<Customer> getAllCustomers() {
        LOGGER.info("Obteniendo todos los clientes desde base de datos");
        return crudGenericService.findWithQuery("SELECT c FROM Customer c");
    }

    public Optional<Customer> findCustomerById(Long id) {
        LOGGER.info("Buscando cliente por ID desde base de datos: " + id);
        Customer customer = crudGenericService.find(Customer.class, id);
        return Optional.ofNullable(customer);
    }

    public void saveCustomer(Customer customer) {
        LOGGER.info("Guardando cliente en base de datos: " + customer.getName());
        if (customer.getId() == null) {
            crudGenericService.create(customer);
        } else {
            crudGenericService.update(customer);
        }
    }

    public void deleteCustomer(Customer customer) {
        LOGGER.info("Eliminando cliente de base de datos: " + customer.getIdCustomer());
        crudGenericService.delete(Customer.class, customer.getId());
    }

    // Supplier operations
    public List<Supplier> getAllSuppliers() {
        LOGGER.info("Obteniendo todos los proveedores desde base de datos");
        return crudGenericService.findWithQuery("SELECT s FROM Supplier s");
    }

    public Optional<Supplier> findSupplierById(Long id) {
        LOGGER.info("Buscando proveedor por ID desde base de datos: " + id);
        Supplier supplier = crudGenericService.find(Supplier.class, id);
        return Optional.ofNullable(supplier);
    }

    public void saveSupplier(Supplier supplier) {
        LOGGER.info("Guardando proveedor en base de datos: " + supplier.getName());
        if (supplier.getId() == null) {
            crudGenericService.create(supplier);
        } else {
            crudGenericService.update(supplier);
        }
    }

    public void deleteSupplier(Supplier supplier) {
        LOGGER.info("Eliminando proveedor de base de datos: " + supplier.getIdSupplier());
        crudGenericService.delete(Supplier.class, supplier.getId());
    }
}