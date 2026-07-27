package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Singleton;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class InventoryService {
    
    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());
    
    private List<Product> products;
    private List<Category> categories;
    private List<Customer> customers;
    private List<Supplier> suppliers;
    
    public InventoryService() {
        initializeData();
    }
    
    private void initializeData() {
        categories = new ArrayList<>();
        try {
            categories.add(new Category(1, "Bebidas"));
            categories.add(new Category(2, "Alimentos"));
            categories.add(new Category(3, "Postres"));
            categories.add(new Category(4, "Snacks"));
        } catch (Exception e) {
            categories = new ArrayList<>();
        }
        
        products = new ArrayList<>();
        Category bebidas = new Category(1, "Bebidas");
        Category alimentos = new Category(2, "Alimentos");
        Category snacks = new Category(3, "Snacks");
        Category postres = new Category(4, "Postres");
        
        products.add(new Product(1, "CAF-001", "Café Americano", "Café americano tradicional", bebidas, "cafe_americano.jpg", 2.50, 1.20, 50, 10));
        products.add(new Product(2, "CAF-002", "Cappuccino", "Cappuccino italiano", bebidas, "cappuccino.jpg", 3.50, 1.80, 30, 8));
        products.add(new Product(3, "POS-001", "Sandwich de Pollo", "Sandwich de pollo con vegetales", alimentos, "sandwich_pollo.jpg", 5.00, 2.50, 25, 5));
        products.add(new Product(4, "POS-002", "Croissant", "Croissant de mantequilla", snacks, "croissant.jpg", 2.00, 0.80, 40, 15));
        products.add(new Product(5, "DES-001", "Cheesecake", "Cheesecake de frutos rojos", postres, "cheesecake.jpg", 4.50, 2.00, 15, 5));
        
        customers = new ArrayList<>();
        customers.add(new Customer(1L, "0912345678", "Juan", "Pérez", "0991234567", "juan.perez@email.com", "Av. Principal 123", "Empresa ABC", 1000.00));
        customers.add(new Customer(2L, "0923456789", "María", "García", "0992345678", "maria.garcia@email.com", "Calle Secundaria 456", "Comercial XYZ", 500.00));
        
        suppliers = new ArrayList<>();
        suppliers.add(new Supplier(1L, "1712345678001", "Distribuidora Central", "Distribuidora Central S.A.", "Carlos Rodríguez", "022345678", "central@distribuidora.com", "Av. Industrial 789"));
        suppliers.add(new Supplier(2L, "1723456789001", "Proveedores del Sur", "Proveedores del Sur Ltda.", "Ana Martínez", "022345679", "sur@proveedores.com", "Calle Comercial 321"));
    }
    
    // Product operations
    @Lock(LockType.READ)
    public List<Product> getAllProducts() {
        LOGGER.info("Obteniendo todos los productos");
        return new ArrayList<>(products);
    }
    
    @Lock(LockType.READ)
    public Optional<Product> findProductById(int id) {
        LOGGER.info("Buscando producto por ID: " + id);
        return products.stream()
                .filter(p -> p.getIdProduct() == id)
                .findFirst();
    }
    
    @Lock(LockType.READ)
    public Optional<Product> findProductByName(String name) {
        LOGGER.info("Buscando producto por nombre: " + name);
        return products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }
    
    @Lock(LockType.READ)
    public List<Product> findProductsByCategory(Category category) {
        if (category == null) {
            return new ArrayList<>(products);
        }
        return products.stream()
                .filter(p -> category.getName().equals(p.getCategory().getName()))
                .toList();
    }
    
    @Lock(LockType.READ)
    public List<Product> findProductsByCategory(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return new ArrayList<>(products);
        }
        return products.stream()
                .filter(p -> categoryName.equals(p.getCategory().getName()))
                .toList();
    }
    
    @Lock(LockType.READ)
    public List<Product> findProductsWithCriticalStock() {
        return products.stream()
                .filter(p -> p.getStock() <= p.getMinStock())
                .toList();
    }
    
    @Lock(LockType.WRITE)
    public void saveProduct(Product product) {
        if (product.getIdProduct() == 0) {
            product.setIdProduct(products.size() + 1);
            products.add(product);
        } else {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getIdProduct() == product.getIdProduct()) {
                    products.set(i, product);
                    break;
                }
            }
        }
    }
    
    @Lock(LockType.WRITE)
    public void deleteProduct(Product product) {
        products.remove(product);
    }
    
    // Category operations
    @Lock(LockType.READ)
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
    
    // Customer operations
    @Lock(LockType.READ)
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }
    
    @Lock(LockType.READ)
    public Optional<Customer> findCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getIdCustomer() == id)
                .findFirst();
    }
    
    @Lock(LockType.WRITE)
    public void saveCustomer(Customer customer) {
        if (customer.getIdCustomer() == 0L) {
            customer.setIdCustomer((long) customers.size() + 1);
            customers.add(customer);
        } else {
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getIdCustomer() == customer.getIdCustomer()) {
                    customers.set(i, customer);
                    break;
                }
            }
        }
    }
    
    @Lock(LockType.WRITE)
    public void deleteCustomer(Customer customer) {
        customers.remove(customer);
    }
    
    // Supplier operations
    @Lock(LockType.READ)
    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers);
    }
    
    @Lock(LockType.READ)
    public Optional<Supplier> findSupplierById(Long id) {
        return suppliers.stream()
                .filter(s -> s.getIdSupplier() == id)
                .findFirst();
    }
    
    @Lock(LockType.WRITE)
    public void saveSupplier(Supplier supplier) {
        if (supplier.getIdSupplier() == 0L) {
            supplier.setIdSupplier((long) suppliers.size() + 1);
            suppliers.add(supplier);
        } else {
            for (int i = 0; i < suppliers.size(); i++) {
                if (suppliers.get(i).getIdSupplier() == supplier.getIdSupplier()) {
                    suppliers.set(i, supplier);
                    break;
                }
            }
        }
    }
    
    @Lock(LockType.WRITE)
    public void deleteSupplier(Supplier supplier) {
        suppliers.remove(supplier);
    }
}
