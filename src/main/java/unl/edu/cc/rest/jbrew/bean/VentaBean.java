package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.SalesService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class VentaBean implements Serializable {
    
    @Inject
    private InventoryFacade inventoryFacade;
    
    @Inject
    private SalesService salesService;
    
    private Product selectedProduct;
    private int selectedQuantity;
    private Customer selectedCustomer;
    private String paymentMethod;
    private double discount;
    
    private List<SalesService.CartItem> cartItems;
    private List<SaleInvoice> saleInvoices;
    
    public VentaBean() {
        this.selectedProduct = new Product();
        this.selectedQuantity = 1;
        this.selectedCustomer = new Customer();
        this.paymentMethod = "efectivo";
        this.discount = 0;
        this.cartItems = List.of();
        this.saleInvoices = List.of();
    }
    
    public void addToCart() {
        if (selectedProduct == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un producto"));
            return;
        }
        
        SalesService.CartOperationResult result = salesService.addToCart(selectedProduct, selectedQuantity);
        
        if (result.isSuccess()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            refreshCartData();
            clearSelection();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", result.getMessage()));
        }
    }
    
    public void agregarAlCarrito() {
        addToCart();
    }
    
    public void removeFromCart(SalesService.CartItem item) {
        SalesService.CartOperationResult result = salesService.removeFromCart(item);
        
        if (result.isSuccess()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            refreshCartData();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", result.getMessage()));
        }
    }
    
    public void eliminarDelCarrito(SalesService.CartItem item) {
        removeFromCart(item);
    }
    
    public void completeSale() {
        SalesService.SaleResult result = salesService.completeSale(selectedCustomer, paymentMethod, discount);
        
        if (result.isSuccess()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            refreshCartData();
            clearSaleFields();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", result.getMessage()));
        }
    }
    
    public void clearCart() {
        salesService.clearCart();
        refreshCartData();
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Carrito limpiado"));
    }
    
    public void limpiarCarrito() {
        clearCart();
    }
    
    public void calcularTotal() {
        // This method is called by AJAX but doesn't need to do anything
        // The total is calculated dynamically in getTotal()
    }
    
    private void refreshCartData() {
        this.cartItems = salesService.getCartItems();
        this.saleInvoices = salesService.getSaleInvoices();
    }
    
    private void clearSelection() {
        this.selectedProduct = null;
        this.selectedQuantity = 1;
    }
    
    private void clearSaleFields() {
        this.selectedCustomer = null;
        this.discount = 0;
    }
    
    // Getters and Setters
    public Product getSelectedProduct() {
        return selectedProduct;
    }
    
    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }
    
    public int getProductoId() {
        return selectedProduct != null ? selectedProduct.getIdProduct() : 0;
    }
    
    public void setProductoId(int productId) {
        Product product = inventoryFacade.findProductById(productId).orElse(null);
        setSelectedProduct(product);
    }
    
    public int getSelectedQuantity() {
        return selectedQuantity;
    }
    
    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }
    
    public int getCantidad() {
        return selectedQuantity;
    }
    
    public void setCantidad(int selectedQuantity) {
        setSelectedQuantity(selectedQuantity);
    }
    
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    public int getClienteId() {
        return selectedCustomer != null ? selectedCustomer.getIdCustomer().intValue() : 0;
    }
    
    public void setClienteId(int customerId) {
        Customer customer = inventoryFacade.findCustomerById(Long.valueOf(customerId)).orElse(null);
        setSelectedCustomer(customer);
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getMetodoPago() {
        return paymentMethod;
    }
    
    public void setMetodoPago(String paymentMethod) {
        setPaymentMethod(paymentMethod);
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    
    public double getDescuento() {
        return discount;
    }
    
    public void setDescuento(double discount) {
        setDiscount(discount);
    }
    
    public List<SalesService.CartItem> getCartItems() {
        if (cartItems.isEmpty()) {
            refreshCartData();
        }
        return cartItems;
    }
    
    public List<SalesService.CartItem> getItemsCarrito() {
        return getCartItems();
    }
    
    public List<SaleInvoice> getSaleInvoices() {
        if (saleInvoices.isEmpty()) {
            refreshCartData();
        }
        return saleInvoices;
    }
    
    public List<SaleInvoice> getFacturas() {
        return getSaleInvoices();
    }
    
    public double getSubtotal() {
        return salesService.getSubtotal();
    }
    
    public double getTax() {
        return salesService.getTax();
    }
    
    public double getIva() {
        return getTax();
    }
    
    public double getTotal() {
        return salesService.getTotalWithDiscount(discount);
    }
    
    public double getTotalWithDiscount() {
        return getTotal();
    }
    
    public List<Product> getAvailableProducts() {
        return inventoryFacade.getAllProducts();
    }
    
    public List<Product> getProductos() {
        return getAvailableProducts();
    }
    
    public List<Customer> getAvailableCustomers() {
        return inventoryFacade.getAllCustomers();
    }
    
    public List<Customer> getClientes() {
        return getAvailableCustomers();
    }
}
