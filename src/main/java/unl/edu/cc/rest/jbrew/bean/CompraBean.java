package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductPriceException;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.io.Serializable;
import java.util.List;

/**
 * Controlador de Compras (compras.xhtml).
 *
 * CAMBIOS respecto a la versión anterior:
 *
 * 1. La pestaña "Adquirir Nuevo Producto" ahora se bindea directamente al
 *    objeto Product completo (compraBean.productoNuevo.codigo,
 *    .name, .salePrice, etc.) en vez de reconstruirlo campo por campo con
 *    ~8 propiedades sueltas en el bean. Esto es justo lo que señaló el
 *    ingeniero: usar el objeto como modelo, no datos sueltos.
 *
 *    Efecto colateral (deseado): esto también arregla el bug de
 *    compilación que tenía la versión anterior. Product.setName(),
 *    setSalePrice() y setStock() declaran excepciones checked
 *    (InvalidProductNameException, etc.). Los antiguos setNombre(),
 *    setPrecioVenta(), setStock() del bean las invocaban sin capturarlas
 *    ni declararlas -> error de compilación. Al bindear la vista
 *    directamente a Product, es JSF (vía reflexión/EL) quien invoca esos
 *    setters, y el compilador ya no exige que este bean las declare.
 *
 * 2. FIX: antes los campos de "Adquirir Nuevo Producto" escribían por
 *    error en selectedProductForRestock (el producto de la OTRA
 *    pestaña), así que newProduct.getName() siempre quedaba null y
 *    registrarCompra() SIEMPRE ejecutaba processRestockPurchase(),
 *    incluso si el usuario estaba en la pestaña de producto nuevo. Ahora
 *    la pestaña activa se seguimiento explícito con
 *    indicePestanaActiva, bindeado al p:tabView.
 *
 * 3. FIX: el precio de compra ingresado en el campo común nunca llegaba
 *    a restockPurchasePrice (la variable que sí se usaba en la llamada
 *    real a purchaseService.processRestockPurchase) ni a
 *    newProduct.purchasePrice -> toda compra se registraba con precio
 *    de compra $0. Ahora precioCompra se aplica explícitamente al
 *    destino correcto según la operación.
 *
 * 4. Se eliminaron los getters/setters no usados por compras.xhtml
 *    (listas de productos/proveedores disponibles, facturas de compra,
 *    accesores duplicados en inglés). Si algún otro XHTML depende de
 *    ellos, avisa y se restauran.
 */
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

    // Pestaña "Reabastecer producto existente"
    private Product productoAReabastecer;
    private int cantidad = 1;

    // Pestaña "Adquirir nuevo producto": el Product completo es el modelo
    // del formulario, no una colección de campos sueltos.
    private Product productoNuevo = new Product();

    // Campo común a ambas pestañas. En cada pestaña alimenta un destino
    // distinto (restockPurchasePrice vs productoNuevo.purchasePrice), así
    // que se maneja como valor simple y se aplica explícitamente en
    // registrarCompra(), no atado directamente a un setter de dominio.
    private double precioCompra;

    private Supplier proveedorSeleccionado;

    private List<PurchaseService.PurchaseRecord> historialCompras = List.of();

    public void registrarCompra() {
        if (proveedorSeleccionado == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un proveedor");
            return;
        }

        PurchaseService.PurchaseResult resultado = (indicePestanaActiva == PESTANA_NUEVO_PRODUCTO)
                ? registrarAdquisicionNuevoProducto()
                : registrarReabastecimiento();

        if (resultado == null) {
            return; // el guard clause correspondiente ya mostró su propio mensaje
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
        }
    }

    private PurchaseService.PurchaseResult registrarReabastecimiento() {
        if (productoAReabastecer == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un producto");
            return null;
        }
        return purchaseService.processRestockPurchase(productoAReabastecer, cantidad, precioCompra, proveedorSeleccionado);
    }

    private PurchaseService.PurchaseResult registrarAdquisicionNuevoProducto() {
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

    // ===== Propiedades usadas por compras.xhtml =====

    public int getIndicePestanaActiva() {
        return indicePestanaActiva;
    }

    public void setIndicePestanaActiva(int indicePestanaActiva) {
        this.indicePestanaActiva = indicePestanaActiva;
    }

    public Integer getProductoId() {
        return productoAReabastecer != null ? productoAReabastecer.getIdProduct() : null;
    }

    public void setProductoId(Integer idProducto) {
        productoAReabastecer = (idProducto == null)
                ? null
                : inventoryFacade.findProductById(idProducto).orElse(null);
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

    /**
     * Puente entre el combo de categoría (que transmite un nombre, un
     * String) y el objeto real Category que necesita productoNuevo.
     * Se aplica directamente sobre productoNuevo, no sobre un campo
     * suelto que después nadie usaba (bug anterior).
     */
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

    /**
     * Genera un código SKU automático a partir de la categoría, ej.
     * "Postres" -> "POST-0001". Si la categoría tiene varias palabras
     * (ej. "Bebidas Calientes"), usa las iniciales de cada una ("BC").
     * El número secuencial se calcula contando cuántos productos
     * existentes ya usan ese mismo prefijo.
     *
     * NOTA: al ser un conteo simple sobre la lista actual de productos,
     * dos altas simultáneas con la misma categoría podrían, en teoría,
     * generar el mismo código (condición de carrera). Para una garantía
     * real de unicidad convendría mover esto a una secuencia de base de
     * datos en InventoryService.
     */
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