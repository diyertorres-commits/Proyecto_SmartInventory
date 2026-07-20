package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class VentaBean implements Serializable {
    
    @Inject
    private InventarioBean inventarioBean;
    
    private int productoId;
    private int cantidad;
    private int clienteId;
    private String metodoPago;
    private double descuento;
    private List<ItemCarrito> itemsCarrito;
    private List<SaleInvoice> facturas;
    private int contadorFacturas;
    
    public VentaBean() {
        this.productoId = 0;
        this.cantidad = 1;
        this.clienteId = 0;
        this.metodoPago = "efectivo";
        this.descuento = 0;
        this.itemsCarrito = new ArrayList<>();
        this.facturas = new ArrayList<>();
        this.contadorFacturas = 1;
    }
    
    public void agregarAlCarrito() {
        try {
            Product producto = inventarioBean.buscarProductoPorId(productoId);
            if (producto == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un producto"));
                return;
            }
            
            if (cantidad > producto.getStock()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Stock insuficiente"));
                return;
            }
            
            // Verificar si ya existe en el carrito
            boolean existe = false;
            for (ItemCarrito item : itemsCarrito) {
                if (item.getProductoId() == productoId) {
                    item.setCantidad(item.getCantidad() + cantidad);
                    item.setSubtotal(item.getCantidad() * item.getPrecio());
                    existe = true;
                    break;
                }
            }
            
            if (!existe) {
                ItemCarrito item = new ItemCarrito();
                item.setProductoId(producto.getIdProduct());
                item.setProductoNombre(producto.getName());
                item.setCantidad(cantidad);
                item.setPrecio(producto.getSalePrice());
                item.setSubtotal(cantidad * producto.getSalePrice());
                itemsCarrito.add(item);
            }
            
            // Reducir stock temporalmente
            producto.modifyStock(-cantidad);
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto agregado al carrito"));
            
            // Resetear selección
            productoId = 0;
            cantidad = 1;
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar al carrito: " + e.getMessage()));
        }
    }
    
    public void eliminarDelCarrito(ItemCarrito item) {
        try {
            // Restaurar stock
            Product producto = inventarioBean.buscarProductoPorId(item.getProductoId());
            if (producto != null) {
                producto.modifyStock(item.getCantidad());
            }
            
            itemsCarrito.remove(item);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado del carrito"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar del carrito: " + e.getMessage()));
        }
    }
    
    public double getTotal() {
        double total = 0;
        for (ItemCarrito item : itemsCarrito) {
            total += item.getSubtotal();
        }
        return total;
    }
    
    public double getSubtotal() {
        return getTotal();
    }
    
    public double getIva() {
        return getSubtotal() * 0.12;
    }
    
    public double getTotalConDescuento() {
        return getSubtotal() + getIva() - descuento;
    }
    
    public void completarVenta() {
        try {
            if (itemsCarrito.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "El carrito está vacío"));
                return;
            }
            
            // Generar factura
            SaleInvoice factura = new SaleInvoice();
            factura.setIdInvoice(contadorFacturas++);
            factura.setInvoiceDate(new Date());
            factura.setInvoiceNumber("FAC-" + String.format("%06d", factura.getIdInvoice()));
            factura.setPaymentMethod(metodoPago);
            
            // Asignar cliente si existe
            if (clienteId != 0) {
                Customer cliente = inventarioBean.buscarClientePorId(clienteId);
                factura.setCustomer(cliente);
            }
            
            facturas.add(factura);
            
            // Limpiar carrito
            itemsCarrito.clear();
            descuento = 0;
            clienteId = 0;
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Venta completada. Factura #" + factura.getInvoiceNumber() + " generada"));
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al completar venta: " + e.getMessage()));
        }
    }
    
    public void limpiarCarrito() {
        // Restaurar stock de todos los items
        for (ItemCarrito item : itemsCarrito) {
            Product producto = inventarioBean.buscarProductoPorId(item.getProductoId());
            if (producto != null) {
                producto.modifyStock(item.getCantidad());
            }
        }
        
        itemsCarrito.clear();
        descuento = 0;
        clienteId = 0;
    }
    
    // Getters y Setters
    public int getProductoId() {
        return productoId;
    }
    
    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public int getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public double getDescuento() {
        return descuento;
    }
    
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
    public List<SaleInvoice> getFacturas() {
        return facturas;
    }
    
    public void setFacturas(List<SaleInvoice> facturas) {
        this.facturas = facturas;
    }
    
    public List<ItemCarrito> getItemsCarrito() {
        return itemsCarrito;
    }
    
    public void setItemsCarrito(List<ItemCarrito> itemsCarrito) {
        this.itemsCarrito = itemsCarrito;
    }
    
    // Clase interna para items del carrito
    public static class ItemCarrito implements Serializable {
        private int productoId;
        private String productoNombre;
        private int cantidad;
        private double precio;
        private double subtotal;
        
        public int getProductoId() {
            return productoId;
        }
        
        public void setProductoId(int productoId) {
            this.productoId = productoId;
        }
        
        public String getProductoNombre() {
            return productoNombre;
        }
        
        public void setProductoNombre(String productoNombre) {
            this.productoNombre = productoNombre;
        }
        
        public int getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
        
        public double getPrecio() {
            return precio;
        }
        
        public void setPrecio(double precio) {
            this.precio = precio;
        }
        
        public double getSubtotal() {
            return subtotal;
        }
        
        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }
    }
}
