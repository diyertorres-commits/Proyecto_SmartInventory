package unl.edu.cc.rest.jbrew.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.People.Customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SalesService {
    
    @Inject
    private InventoryService inventoryService;
    
    private List<CartItem> cartItems;
    private List<SaleInvoice> saleInvoices;
    private int invoiceCounter;
    
    public SalesService() {
        this.cartItems = new ArrayList<>();
        this.saleInvoices = new ArrayList<>();
        this.invoiceCounter = 1;
    }
    
    public CartOperationResult addToCart(Product product, int quantity) {
        Optional<Product> productOpt = inventoryService.findProductById(product.getIdProduct());
        if (productOpt.isEmpty()) {
            return new CartOperationResult(false, "Producto no encontrado", null);
        }
        
        Product existingProduct = productOpt.get();
        
        if (quantity > existingProduct.getStock()) {
            return new CartOperationResult(false, "Stock insuficiente", null);
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getProductoId() == product.getIdProduct())
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCantidad(item.getCantidad() + quantity);
            item.setSubtotal(item.getCantidad() * item.getPrecio());
        } else {
            CartItem item = new CartItem();
            item.setProductoId(product.getIdProduct());
            item.setProductoNombre(product.getName());
            item.setCantidad(quantity);
            item.setPrecio(product.getSalePrice());
            item.setSubtotal(quantity * product.getSalePrice());
            cartItems.add(item);
        }
        
        // Temporarily reduce stock
        existingProduct.modifyStock(-quantity);
        
        return new CartOperationResult(true, "Producto agregado al carrito", existingItem.orElse(null));
    }
    
    public CartOperationResult removeFromCart(CartItem item) {
        // Restore stock
        Optional<Product> productOpt = inventoryService.findProductById(item.getProductoId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.modifyStock(item.getCantidad());
        }
        
        cartItems.remove(item);
        return new CartOperationResult(true, "Producto eliminado del carrito", null);
    }
    
    public void clearCart() {
        // Restore stock for all items
        for (CartItem item : cartItems) {
            Optional<Product> productOpt = inventoryService.findProductById(item.getProductoId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.modifyStock(item.getCantidad());
            }
        }
        cartItems.clear();
    }
    
    public SaleResult completeSale(Customer customer, String paymentMethod, double discount) {
        if (cartItems.isEmpty()) {
            return new SaleResult(false, "El carrito está vacío", null);
        }
        
        SaleInvoice invoice = new SaleInvoice();
        invoice.setIdInvoice(invoiceCounter++);
        invoice.setInvoiceDate(new Date());
        invoice.setInvoiceNumber("FAC-" + String.format("%06d", invoice.getIdInvoice()));
        invoice.setPaymentMethod(paymentMethod);
        
        if (customer != null) {
            invoice.setCustomer(customer);
        }
        
        saleInvoices.add(invoice);
        
        // Clear cart after successful sale
        cartItems.clear();
        
        return new SaleResult(true, "Venta completada. Factura #" + invoice.getInvoiceNumber() + " generada", invoice);
    }
    
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }
    
    public List<SaleInvoice> getSaleInvoices() {
        return new ArrayList<>(saleInvoices);
    }
    
    public double getSubtotal() {
        return cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
    
    public double getTax() {
        return getSubtotal() * 0.12;
    }
    
    public double getTotalWithDiscount(double discount) {
        return getSubtotal() + getTax() - discount;
    }
    
    // Cart item data class
    public static class CartItem {
        private int productoId;
        private String productoNombre;
        private int cantidad;
        private double precio;
        private double subtotal;
        
        public int getProductoId() { return productoId; }
        public void setProductoId(int productoId) { this.productoId = productoId; }
        
        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
        
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        
        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }
        
        public double getSubtotal() { return subtotal; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    }
    
    public static class CartOperationResult {
        private final boolean success;
        private final String message;
        private final CartItem item;
        
        public CartOperationResult(boolean success, String message, CartItem item) {
            this.success = success;
            this.message = message;
            this.item = item;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public CartItem getItem() { return item; }
    }
    
    public static class SaleResult {
        private final boolean success;
        private final String message;
        private final SaleInvoice invoice;
        
        public SaleResult(boolean success, String message, SaleInvoice invoice) {
            this.success = success;
            this.message = message;
            this.invoice = invoice;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public SaleInvoice getInvoice() { return invoice; }
    }
}
