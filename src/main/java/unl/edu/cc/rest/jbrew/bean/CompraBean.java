package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductNameException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductPriceException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductStockException;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class CompraBean implements Serializable {
    
    @Inject
    private InventarioBean inventarioBean;
    
    // Campos para REABASTECER
    private int productoId;
    private int cantidad;
    
    // Campos para ADQUIRIR NUEVO PRODUCTO
    private String codigo;
    private String nombre;
    private String categoria;
    private String descripcion;
    private double precioVenta;
    private int stock;
    private int stockMinimo;
    private String imagen;
    
    // Campos comunes
    private int proveedorId;
    private double precioCompra;
    
    // Historial de compras y facturas
    private List<Compra> compras;
    private List<PurchaseInvoice> facturas;
    private int contadorFacturas;
    
    public CompraBean() {
        this.productoId = 0;
        this.cantidad = 1;
        this.codigo = "";
        this.nombre = "";
        this.categoria = "";
        this.descripcion = "";
        this.precioVenta = 0;
        this.stock = 0;
        this.stockMinimo = 0;
        this.imagen = "";
        this.proveedorId = 0;
        this.precioCompra = 0;
        this.compras = new ArrayList<>();
        this.facturas = new ArrayList<>();
        this.contadorFacturas = 1;
    }
    
    public String registrarCompra() {
        try {
            // Verificar que se haya seleccionado un proveedor
            if (proveedorId == 0) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un proveedor"));
                return null;
            }
            
            Supplier proveedor = inventarioBean.buscarProveedorPorId(proveedorId);
            
            // Determinar el tipo de compra basado en si hay productoId
            if (productoId != 0) {
                // REABASTECER producto existente
                Product producto = inventarioBean.buscarProductoPorId(productoId);
                if (producto == null) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Producto no encontrado"));
                    return null;
                }
                
                // Actualizar stock
                producto.modifyStock(cantidad);
                producto.setPurchasePrice(precioCompra);
                
                // Registrar en historial
                Compra compra = new Compra();
                compra.setId(compras.size() + 1);
                compra.setFecha(new Date());
                compra.setTipo("REABASTECER");
                compra.setProductoNombre(producto.getName());
                compra.setCantidad(cantidad);
                compra.setPrecioCompra(precioCompra);
                compra.setTotal(cantidad * precioCompra);
                compra.setProveedorNombre(proveedor.getName());
                compras.add(compra);
                
                // Generar factura de compra
                PurchaseInvoice factura = new PurchaseInvoice();
                factura.setIdInvoice(contadorFacturas++);
                factura.setInvoiceDate(new Date());
                factura.setInvoiceNumber("FAC-COMP-" + String.format("%06d", factura.getIdInvoice()));
                factura.setPurchaseOrderNumber("PO-" + String.format("%06d", contadorFacturas));
                factura.setSupplier(proveedor);
                facturas.add(factura);
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Stock reabastecido correctamente. Factura #" + factura.getInvoiceNumber() + " generada"));
                
            } else {
                // ADQUIRIR nuevo producto
                if (codigo.isEmpty() || nombre.isEmpty() || categoria.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Complete los campos obligatorios"));
                    return null;
                }
                
                // Crear nuevo producto
                Product nuevoProducto = new Product(
                    inventarioBean.getProductos().size() + 1,
                    codigo,
                    nombre,
                    descripcion,
                    categoria,
                    imagen,
                    precioVenta,
                    precioCompra,
                    stock,
                    stockMinimo
                );
                
                inventarioBean.getProductos().add(nuevoProducto);
                
                // Registrar en historial
                Compra compra = new Compra();
                compra.setId(compras.size() + 1);
                compra.setFecha(new Date());
                compra.setTipo("ADQUIRIR");
                compra.setProductoNombre(nombre);
                compra.setCantidad(stock);
                compra.setPrecioCompra(precioCompra);
                compra.setTotal(stock * precioCompra);
                compra.setProveedorNombre(proveedor.getName());
                compras.add(compra);
                
                // Generar factura de compra
                PurchaseInvoice factura = new PurchaseInvoice();
                factura.setIdInvoice(contadorFacturas++);
                factura.setInvoiceDate(new Date());
                factura.setInvoiceNumber("FAC-COMP-" + String.format("%06d", factura.getIdInvoice()));
                factura.setPurchaseOrderNumber("PO-" + String.format("%06d", contadorFacturas));
                factura.setSupplier(proveedor);
                facturas.add(factura);
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Nuevo producto adquirido correctamente. Factura #" + factura.getInvoiceNumber() + " generada"));
            }
            
            // Limpiar campos
            limpiarCampos();
            
            return null;
        } catch (InvalidProductNameException | InvalidProductPriceException | InvalidProductStockException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error de validación: " + e.getMessage()));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar compra: " + e.getMessage()));
            return null;
        }
    }
    
    private void limpiarCampos() {
        productoId = 0;
        cantidad = 1;
        codigo = "";
        nombre = "";
        categoria = "";
        descripcion = "";
        precioVenta = 0;
        stock = 0;
        stockMinimo = 0;
        imagen = "";
        proveedorId = 0;
        precioCompra = 0;
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
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public double getPrecioVenta() {
        return precioVenta;
    }
    
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public int getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
    
    public String getImagen() {
        return imagen;
    }
    
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    public int getProveedorId() {
        return proveedorId;
    }
    
    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }
    
    public double getPrecioCompra() {
        return precioCompra;
    }
    
    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }
    
    public List<Compra> getCompras() {
        return compras;
    }
    
    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
    
    public double getTotalCompras() {
        double total = 0;
        for (Compra compra : compras) {
            total += compra.getTotal();
        }
        return total;
    }
    
    public List<PurchaseInvoice> getFacturas() {
        return facturas;
    }
    
    public void setFacturas(List<PurchaseInvoice> facturas) {
        this.facturas = facturas;
    }
    
    // Clase interna para compras
    public static class Compra implements Serializable {
        private int id;
        private Date fecha;
        private String tipo;
        private String productoNombre;
        private int cantidad;
        private double precioCompra;
        private double total;
        private String proveedorNombre;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public Date getFecha() {
            return fecha;
        }
        
        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }
        
        public String getTipo() {
            return tipo;
        }
        
        public void setTipo(String tipo) {
            this.tipo = tipo;
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
        
        public double getPrecioCompra() {
            return precioCompra;
        }
        
        public void setPrecioCompra(double precioCompra) {
            this.precioCompra = precioCompra;
        }
        
        public double getTotal() {
            return total;
        }
        
        public void setTotal(double total) {
            this.total = total;
        }
        
        public String getProveedorNombre() {
            return proveedorNombre;
        }
        
        public void setProveedorNombre(String proveedorNombre) {
            this.proveedorNombre = proveedorNombre;
        }
        
        public String getFechaTexto() {
            if (fecha != null) {
                return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
            }
            return "";
        }
    }
}
