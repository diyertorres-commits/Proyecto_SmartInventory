package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class InventarioBean {
    
    private List<Product> productos;
    private List<Category> categorias;
    private List<Customer> clientes;
    private List<Supplier> proveedores;
    
    public InventarioBean() {
        inicializarDatos();
    }
    
    private void inicializarDatos() {
        // Inicializar categorías
        categorias = new ArrayList<>();
        categorias.add(new Category(1, "Bebidas"));
        categorias.add(new Category(2, "Alimentos"));
        categorias.add(new Category(3, "Postres"));
        categorias.add(new Category(4, "Snacks"));
        
        // Inicializar productos
        productos = new ArrayList<>();
        productos.add(new Product(1, "CAF-001", "Café Americano", "Café americano tradicional", "Bebidas", "cafe_americano.jpg", 2.50, 1.20, 50, 10));
        productos.add(new Product(2, "CAF-002", "Cappuccino", "Cappuccino italiano", "Bebidas", "cappuccino.jpg", 3.50, 1.80, 30, 8));
        productos.add(new Product(3, "POS-001", "Sandwich de Pollo", "Sandwich de pollo con vegetales", "Alimentos", "sandwich_pollo.jpg", 5.00, 2.50, 25, 5));
        productos.add(new Product(4, "POS-002", "Croissant", "Croissant de mantequilla", "Snacks", "croissant.jpg", 2.00, 0.80, 40, 15));
        productos.add(new Product(5, "DES-001", "Cheesecake", "Cheesecake de frutos rojos", "Postres", "cheesecake.jpg", 4.50, 2.00, 15, 5));
        
        // Inicializar clientes
        clientes = new ArrayList<>();
        clientes.add(new Customer(1, "0912345678", "Juan", "Pérez", "0991234567", "juan.perez@email.com", "Av. Principal 123", "Empresa ABC", 1000.00));
        clientes.add(new Customer(2, "0923456789", "María", "García", "0992345678", "maria.garcia@email.com", "Calle Secundaria 456", "Comercial XYZ", 500.00));
        
        // Inicializar proveedores
        proveedores = new ArrayList<>();
        proveedores.add(new Supplier(1, "1712345678001", "Distribuidora Central", "Distribuidora Central S.A.", "Carlos Rodríguez", "022345678", "central@distribuidora.com", "Av. Industrial 789"));
        proveedores.add(new Supplier(2, "1723456789001", "Proveedores del Sur", "Proveedores del Sur Ltda.", "Ana Martínez", "022345679", "sur@proveedores.com", "Calle Comercial 321"));
    }
    
    // Getters para las listas
    public List<Product> getProductos() {
        return productos;
    }
    
    public List<Category> getCategorias() {
        return categorias;
    }
    
    public List<Customer> getClientes() {
        return clientes;
    }
    
    public List<Supplier> getProveedores() {
        return proveedores;
    }
    
    // Métodos auxiliares
    public List<Product> getProductosPorCategoria(String categoria) {
        List<Product> productosCategoria = new ArrayList<>();
        for (Product p : productos) {
            if (categoria == null || categoria.equals(p.getCategoria())) {
                productosCategoria.add(p);
            }
        }
        return productosCategoria;
    }
    
    public List<Product> getProductosStockCritico() {
        List<Product> stockCritico = new ArrayList<>();
        for (Product p : productos) {
            if (p.getStock() <= p.getMinStock()) {
                stockCritico.add(p);
            }
        }
        return stockCritico;
    }
    
    public Product buscarProductoPorId(int id) {
        for (Product p : productos) {
            if (p.getIdProduct() == id) {
                return p;
            }
        }
        return null;
    }
    
    public Customer buscarClientePorId(int id) {
        for (Customer c : clientes) {
            if (c.getIdCustomer() == id) {
                return c;
            }
        }
        return null;
    }
    
    public Supplier buscarProveedorPorId(int id) {
        for (Supplier s : proveedores) {
            if (s.getIdSupplier() == id) {
                return s;
            }
        }
        return null;
    }
}
