package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductPriceException;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CompraBean implements Serializable {

    public static final int PESTANA_REABASTECER = 0;
    public static final int PESTANA_NUEVO_PRODUCTO = 1;

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private PurchaseService purchaseService;

    private int indicePestanaActiva = PESTANA_REABASTECER;

    private Product productoAReabastecer;
    private int cantidad = 1;

    private Product productoNuevo = new Product();

    private double precioCompra;

    private Supplier proveedorSeleccionado;

    private List<PurchaseService.PurchaseRecord> historialCompras = List.of();

    private PurchaseService.PurchaseRecord compraSeleccionada;

    public void registrarCompra() {
        if (proveedorSeleccionado == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un proveedor");
            return;
        }

        PurchaseService.PurchaseResult resultado = (indicePestanaActiva == PESTANA_NUEVO_PRODUCTO)
                ? registrarAdquisicionNuevoProducto()
                : registrarReabastecimiento();

        if (resultado == null) {
            return;
        }

        mostrarMensaje(
                resultado.isSuccess() ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR,
                resultado.isSuccess() ? "Éxito" : "Error",
                resultado.getMessage());

        if (resultado.isSuccess()) {
            refrescarHistorial();
            if (indicePestanaActiva == PESTANA_NUEVO_PRODUCTO) {
                limpiarProductoNuevo();
            } else {
                limpiarReabastecimiento();
            }
            PrimeFaces.current().ajax().addCallbackParam("success", true);
        } else {
            PrimeFaces.current().ajax().addCallbackParam("success", false);
        }
    }

    public void alCambiarPestana(org.primefaces.event.TabChangeEvent event) {
    }

    private PurchaseService.PurchaseResult registrarReabastecimiento() {
        if (productoAReabastecer == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un producto");
            return null;
        }
        if (precioCompra <= 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Ingrese un precio de compra válido");
            return null;
        }
        return purchaseService.processRestockPurchase(productoAReabastecer, cantidad, precioCompra, proveedorSeleccionado);
    }

    private PurchaseService.PurchaseResult registrarAdquisicionNuevoProducto() {
        if (productoNuevo.getName() == null || productoNuevo.getName().isBlank()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Ingrese el nombre del producto");
            return null;
        }
        if (productoNuevo.getCategory() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione una categoría");
            return null;
        }
        if (productoNuevo.getSalePrice() <= 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Ingrese un precio de venta válido");
            return null;
        }
        if (precioCompra <= 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Ingrese un precio de compra válido");
            return null;
        }

        try {
            productoNuevo.setPurchasePrice(precioCompra);
        } catch (InvalidProductPriceException e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            return null;
        }
        return purchaseService.processNewProductPurchase(productoNuevo, proveedorSeleccionado);
    }

    private void refrescarHistorial() {
        this.historialCompras = purchaseService.getPurchaseHistory();
    }

    private void limpiarReabastecimiento() {
        this.productoAReabastecer = null;
        this.cantidad = 1;
        this.precioCompra = 0;
    }

    private void limpiarProductoNuevo() {
        this.productoNuevo = new Product();
        this.precioCompra = 0;
    }

    private void mostrarMensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    // Impresión de comprobante de compra

    public void prepararImpresionCompra(PurchaseService.PurchaseRecord compra) {
        this.compraSeleccionada = compra;
    }

    public PurchaseService.PurchaseRecord getCompraSeleccionada() {
        return compraSeleccionada;
    }

    // Propiedades usadas por compras.xhtml

    public int getIndicePestanaActiva() {
        return indicePestanaActiva;
    }

    public void setIndicePestanaActiva(int indicePestanaActiva) {
        this.indicePestanaActiva = indicePestanaActiva;
    }

    public Integer getProductoId() {
        return productoAReabastecer != null ? productoAReabastecer.getIdProduct() : null;
    }

    public Product getProductoAReabastecer() {
        return productoAReabastecer;
    }

    public void setProductoId(Integer idProducto) {
        productoAReabastecer = (idProducto == null)
                ? null
                : inventoryFacade.findProductById(idProducto).orElse(null);
        
        // Cargar el precio de compra actual del producto seleccionado
        if (productoAReabastecer != null) {
            this.precioCompra = productoAReabastecer.getPurchasePrice();
        } else {
            this.precioCompra = 0;
        }
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Product getProductoNuevo() {
        return productoNuevo;
    }

    public void setProductoNuevo(Product productoNuevo) {
        this.productoNuevo = productoNuevo;
    }

    public String getCategoryName() {
        return productoNuevo.getCategory() != null ? productoNuevo.getCategory().getName() : null;
    }

    public void setCategoryName(String categoryName) {
        Category categoria = (categoryName == null)
                ? null
                : inventoryFacade.getAllCategories().stream()
                .filter(c -> c.getName().equals(categoryName))
                .findFirst()
                .orElse(null);
        productoNuevo.setCategory(categoria);
        productoNuevo.setCodigo(categoria != null ? generarCodigo(categoria) : null);
    }

    private String generarCodigo(Category categoria) {
        String prefijo = obtenerPrefijo(categoria.getName());
        long cantidadExistente = inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getCodigo() != null && p.getCodigo().startsWith(prefijo + "-"))
                .count();
        return prefijo + "-" + String.format("%04d", cantidadExistente + 1);
    }

    private String obtenerPrefijo(String nombreCategoria) {
        String[] palabras = nombreCategoria.trim().split("\\s+");
        if (palabras.length > 1) {
            StringBuilder iniciales = new StringBuilder();
            for (String palabra : palabras) {
                if (!palabra.isEmpty()) {
                    iniciales.append(Character.toUpperCase(palabra.charAt(0)));
                }
            }
            return iniciales.toString();
        }
        String palabra = palabras[0].toUpperCase();
        return palabra.substring(0, Math.min(4, palabra.length()));
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Long getProveedorId() {
        return proveedorSeleccionado != null ? proveedorSeleccionado.getIdSupplier() : null;
    }

    public void setProveedorId(Long idProveedor) {
        proveedorSeleccionado = (idProveedor == null)
                ? null
                : inventoryFacade.findSupplierById(idProveedor).orElse(null);
    }

    public List<PurchaseService.PurchaseRecord> getCompras() {
        if (historialCompras.isEmpty()) {
            refrescarHistorial();
        }
        return historialCompras;
    }

    public double getTotalCompras() {
        return purchaseService.getTotalPurchases();
    }
}