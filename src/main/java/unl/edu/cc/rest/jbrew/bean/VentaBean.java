package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
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
    private InventoryService inventoryService;
    
    @Inject
    private SalesService salesService;
    
    // Objects for sale operations
    private Product selectedProduct;
    private int selectedQuantity;
    private Customer selectedCustomer;
    private String paymentMethod;
    private double discount;
    
    // Cart and invoices
    private List<SalesService.CartItem> cartItems;
    private List<SaleInvoice> saleInvoices;
    
    public VentaBean() {
        this.selectedProduct = null;
        this.selectedQuantity = 1;
        this.selectedCustomer = null;
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
    
    public double getSubtotal() {
        return salesService.getSubtotal();
    }
    
    public double getTax() {
        return salesService.getTax();
    }
    
    public double getTotalWithDiscount() {
        return salesService.getTotalWithDiscount(discount);
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
    
    public Product getProductoSeleccionado() {
        return getSelectedProduct();
    }
    
    public int getProductoId() {
        return selectedProduct != null ? selectedProduct.getIdProduct() : 0;
    }
    
    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }
    
    public void setProductoSeleccionado(Product selectedProduct) {
        setSelectedProduct(selectedProduct);
    }
    
    public void setProductoId(int productId) {
        Product product = inventoryService.findProductById(productId).orElse(null);
        setSelectedProduct(product);
    }
    
    public int getSelectedQuantity() {
        return selectedQuantity;
    }
    
    public int getCantidad() {
        return selectedQuantity;
    }
    
    public int getCantidadSeleccionada() {
        return getSelectedQuantity();
    }
    
    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }
    
    public void setCantidad(int selectedQuantity) {
        setSelectedQuantity(selectedQuantity);
    }
    
    public void setCantidadSeleccionada(int selectedQuantity) {
        setSelectedQuantity(selectedQuantity);
    }
    
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public Customer getClienteSeleccionado() {
        return getSelectedCustomer();
    }
    
    public Customer getCliente() {
        return getSelectedCustomer();
    }
    
    public int getClienteId() {
        return selectedCustomer != null ? selectedCustomer.getIdCustomer() : 0;
    }
    
    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    public void setClienteSeleccionado(Customer selectedCustomer) {
        setSelectedCustomer(selectedCustomer);
    }
    
    public void setClienteId(int customerId) {
        Customer customer = inventoryService.findCustomerById(customerId).orElse(null);
        setSelectedCustomer(customer);
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getMetodoPago() {
        return getPaymentMethod();
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public void setMetodoPago(String paymentMethod) {
        setPaymentMethod(paymentMethod);
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public double getDescuento() {
        return getDiscount();
    }
    
    public double getIva() {
        return salesService.getTax();
    }
    
    public double getTotal() {
        return salesService.getTotalWithDiscount(discount);
    }
    
    public void setDiscount(double discount) {
        this.discount = discount;
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
    
    public List<SalesService.CartItem> getCarrito() {
        return getCartItems();
    }
    
    public List<SaleInvoice> getSaleInvoices() {
        if (saleInvoices.isEmpty()) {
            refreshCartData();
        }
        return saleInvoices;
    }
    
    public List<Product> getAvailableProducts() {
        return inventoryService.getAllProducts();
    }
    
    public List<Product> getProductosDisponibles() {
        return getAvailableProducts();
    }
    
    public List<Product> getProductos() {
        return getAvailableProducts();
    }
    
    public List<Customer> getAvailableCustomers() {
        return inventoryService.getAllCustomers();
    }
    
    public List<Customer> getClientes() {
        return getAvailableCustomers();
    }
    
    public List<Customer> getClientesDisponibles() {
        return getAvailableCustomers();
    }
    
    // Compatibility method for ReporteBean
    public List<SaleInvoice> getFacturas() {
        return getSaleInvoices();
    }
}
