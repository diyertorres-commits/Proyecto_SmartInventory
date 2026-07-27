package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.CarritoService;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.ResultadoCarrito;
import unl.edu.cc.rest.jbrew.business.ResultadoVenta;
import unl.edu.cc.rest.jbrew.business.VentaService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.Sales.Carrito;
import unl.edu.cc.rest.jbrew.domain.Sales.ItemCarrito;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class VentaBean implements Serializable {

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private CarritoService carritoService;

    @Inject
    private VentaService ventaService;

    private final Carrito carrito = new Carrito();

    private Product productoSeleccionado;
    private int cantidadSeleccionada = 1;
    private Customer clienteSeleccionado;
    private String metodoPago = "efectivo";
    private double descuento = 0;

    private List<SaleInvoice> facturas = List.of();

    // ===== Acciones de la vista =====

    public void agregarAlCarrito() {
        if (productoSeleccionado == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un producto");
            return;
        }

        ResultadoCarrito resultado = carritoService.agregarProducto(carrito, productoSeleccionado, cantidadSeleccionada);
        mostrarResultadoCarrito(resultado);
        if (resultado.isExitoso()) {
            limpiarSeleccionDeProducto();
        }
    }

    public void eliminarDelCarrito(ItemCarrito item) {
        ResultadoCarrito resultado = carritoService.eliminarProducto(carrito, item);
        mostrarResultadoCarrito(resultado);
    }

    public void completarVenta() {
        ResultadoVenta resultado = ventaService.registrarVenta(carrito, clienteSeleccionado, metodoPago, descuento);
        mostrarResultadoVenta(resultado);
        if (resultado.isExitoso()) {
            facturas = ventaService.obtenerFacturas();
            limpiarDatosDeVenta();
        }
    }

    public void limpiarCarrito() {
        carrito.vaciar();
        mostrarMensaje(FacesMessage.SEVERITY_INFO, "Info", "Carrito limpiado");
    }

    public void calcularTotal() {
        // Disparado por AJAX; el total se recalcula dinámicamente en getTotal()
    }

    // ===== Helpers privados (sin lógica de negocio, solo orquestación de UI) =====

    private void limpiarSeleccionDeProducto() {
        productoSeleccionado = null;
        cantidadSeleccionada = 1;
    }

    private void limpiarDatosDeVenta() {
        clienteSeleccionado = null;
        descuento = 0;
    }

    private void mostrarResultadoCarrito(ResultadoCarrito resultado) {
        mostrarMensaje(
                resultado.isExitoso() ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR,
                resultado.isExitoso() ? "Éxito" : "Error",
                resultado.getMensaje());
    }

    private void mostrarResultadoVenta(ResultadoVenta resultado) {
        mostrarMensaje(
                resultado.isExitoso() ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR,
                resultado.isExitoso() ? "Éxito" : "Error",
                resultado.getMensaje());
    }

    private void mostrarMensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    // ===== Propiedades expuestas a la vista =====
    // Un solo nombre por propiedad (español, consistente con el resto del
    // dominio y con lo que ya usa venta.xhtml) — sin duplicados en inglés.

    public Integer getProductoId() {
        return productoSeleccionado != null ? productoSeleccionado.getIdProduct() : null;
    }

    public void setProductoId(Integer idProducto) {
        productoSeleccionado = (idProducto == null)
                ? null
                : inventoryFacade.findProductById(idProducto).orElse(null);
    }

    public int getCantidad() {
        return cantidadSeleccionada;
    }

    public void setCantidad(int cantidad) {
        this.cantidadSeleccionada = cantidad;
    }

    public Long getClienteId() {
        return clienteSeleccionado != null ? clienteSeleccionado.getIdCustomer() : null;
    }

    public void setClienteId(Long idCliente) {
        clienteSeleccionado = (idCliente == null)
                ? null
                : inventoryFacade.findCustomerById(idCliente).orElse(null);
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

    public List<ItemCarrito> getItemsCarrito() {
        return carrito.getItems();
    }

    public List<SaleInvoice> getFacturas() {
        if (facturas.isEmpty()) {
            facturas = ventaService.obtenerFacturas();
        }
        return facturas;
    }

    public double getSubtotal() {
        return carrito.calcularSubtotal();
    }

    public double getIva() {
        return carrito.calcularIva();
    }

    public double getTotal() {
        return carrito.calcularTotal(descuento);
    }

    public List<Product> getProductos() {
        return inventoryFacade.getAllProducts();
    }

    public List<Customer> getClientes() {
        return inventoryFacade.getAllCustomers();
    }
}