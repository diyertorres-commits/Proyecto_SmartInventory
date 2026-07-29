package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.ProductCodeService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.CompraRequest;
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
    private ProductCodeService productCodeService;

    @Inject
    private PurchaseService purchaseService;

    private int indicePestanaActiva = PESTANA_REABASTECER;

    private Product productoAReabastecer;
    private CompraRequest compraRequest = new CompraRequest();

    private Product productoNuevo = new Product();
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
        return purchaseService.processRestockPurchase(productoAReabastecer, compraRequest.getCantidad(), compraRequest.getPrecioCompra(), proveedorSeleccionado);
    }

    private PurchaseService.PurchaseResult registrarAdquisicionNuevoProducto() {
        // Validar precio de compra antes de intentar establecerlo
        if (compraRequest.getPrecioCompra() <= 0) {
            return new PurchaseService.PurchaseResult(false, "El precio de compra debe ser mayor que cero", null, null);
        }
        
        try {
            productoNuevo.setPurchasePrice(compraRequest.getPrecioCompra());
        } catch (InvalidProductPriceException e) {
            return new PurchaseService.PurchaseResult(false, e.getMessage(), null, null);
        }
        return purchaseService.processNewProductPurchase(productoNuevo, proveedorSeleccionado);
    }

    private void refrescarHistorial() {
        this.historialCompras = purchaseService.getPurchaseHistory();
    }

    private void limpiarReabastecimiento() {
        this.productoAReabastecer = null;
        this.compraRequest = new CompraRequest();
    }

    private void limpiarProductoNuevo() {
        this.productoNuevo = new Product();
        this.compraRequest = new CompraRequest();
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
            this.compraRequest.setPrecioCompra(productoAReabastecer.getPurchasePrice());
        } else {
            this.compraRequest.setPrecioCompra(0);
        }
    }

    public int getCantidad() {
        return compraRequest.getCantidad();
    }

    public void setCantidad(int cantidad) {
        compraRequest.setCantidad(cantidad);
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
        productoNuevo.setCodigo(categoria != null ? productCodeService.generateCode(categoria) : null);
    }

    public double getPrecioCompra() {
        return compraRequest.getPrecioCompra();
    }

    public void setPrecioCompra(double precioCompra) {
        compraRequest.setPrecioCompra(precioCompra);
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