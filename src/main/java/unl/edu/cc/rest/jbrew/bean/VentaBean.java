package unl.edu.cc.rest.jbrew.bean;

import jakarta.annotation.PostConstruct;
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
import unl.edu.cc.rest.jbrew.domain.Movements.ProductMovement;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.Sales.Carrito;
import unl.edu.cc.rest.jbrew.domain.Sales.ItemCarrito;
import unl.edu.cc.rest.jbrew.domain.Sales.VentaDTO;

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

    private Carrito carrito;
    private VentaDTO ventaDTO;
    private List<SaleInvoice> facturas;
    private String ordenFacturas = "recientes";

    // Factura seleccionada actualmente para ver/imprimir el detalle
    private SaleInvoice facturaSeleccionada;

    @PostConstruct
    public void inicializar() {
        this.carrito = new Carrito();
        this.ventaDTO = new VentaDTO();
        this.facturas = ventaService.obtenerFacturas(ordenFacturas);
    }

    // ===== Acciones de la vista =====

    public void agregarProductoAlCarrito() {
        if (ventaDTO.getProductoSeleccionado() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un producto");
            return;
        }

        ResultadoCarrito resultado = carritoService.agregarProducto(
            carrito, 
            ventaDTO.getProductoSeleccionado(), 
            ventaDTO.getCantidadSeleccionada()
        );
        mostrarResultadoCarrito(resultado);
        if (resultado.isExitoso()) {
            ventaDTO.limpiarSeleccionProducto();
        }
    }

    public void eliminarProductoDelCarrito(ItemCarrito item) {
        ResultadoCarrito resultado = carritoService.eliminarProducto(carrito, item);
        mostrarResultadoCarrito(resultado);
    }

    public void procesarVenta() {
        ResultadoVenta resultado = ventaService.registrarVenta(
            carrito, 
            ventaDTO.getClienteSeleccionado(), 
            ventaDTO.getMetodoPago(), 
            ventaDTO.getDescuento()
        );
        mostrarResultadoVenta(resultado);
        if (resultado.isExitoso()) {
            this.facturas = ventaService.obtenerFacturas(ordenFacturas);
            ventaDTO.limpiarDatosVenta();
        }
    }

    // Método alias para compatibilidad con vistas antiguas
    public void completarVenta() {
        procesarVenta();
    }

    public void vaciarCarrito() {
        carrito.vaciar();
        mostrarMensaje(FacesMessage.SEVERITY_INFO, "Info", "Carrito limpiado");
    }

    public void recalcularTotal() {
        // Disparado por AJAX; el total se recalcula dinámicamente en getTotal()
    }

    // ===== Impresión de factura =====

    public void prepararImpresion(SaleInvoice factura) {
        this.facturaSeleccionada = factura;
    }

    public SaleInvoice getFacturaSeleccionada() {
        return facturaSeleccionada;
    }

    public List<ProductMovement> getDetalleFacturaSeleccionada() {
        if (facturaSeleccionada == null || facturaSeleccionada.getMovement() == null) {
            return List.of();
        }
        return facturaSeleccionada.getMovement().getProductMovementList();
    }

    public double getSubtotalFacturaSeleccionada() {
        return facturaSeleccionada != null ? facturaSeleccionada.getSubtotal() : 0;
    }

    public double getIvaFacturaSeleccionada() {
        return facturaSeleccionada != null ? facturaSeleccionada.getTax() : 0;
    }

    public double getDescuentoFacturaSeleccionada() {
        return facturaSeleccionada != null ? facturaSeleccionada.getDiscount() : 0;
    }

    public double getTotalFacturaSeleccionada() {
        return facturaSeleccionada != null ? facturaSeleccionada.getTotal() : 0;
    }

    public String getClienteFacturaSeleccionada() {
        if (facturaSeleccionada == null || facturaSeleccionada.getCustomer() == null) {
            return "Cliente Mostrador";
        }
        return facturaSeleccionada.getCustomer().getName();
    }

    public void restaurarCarritoDesdeJson(String carritoJson) {
        try {
            if (carritoJson != null && !carritoJson.isEmpty()) {
                // Parsear JSON simple (sin usar librerías externas)
                String[] parts = carritoJson.split("\"items\":\\[");
                if (parts.length > 1) {
                    String itemsPart = parts[1].split("\\],\"descuento\"")[0];
                    String[] itemStrings = itemsPart.split("\\},\\{");
                    
                    carrito.vaciar();
                    
                    for (String itemStr : itemStrings) {
                        // Extraer datos del item
                        String nombre = extraerValor(itemStr, "productoNombre");
                        int cantidad = Integer.parseInt(extraerValor(itemStr, "cantidad"));
                        double precio = Double.parseDouble(extraerValor(itemStr, "precio"));
                        
                        // Buscar producto por nombre
                        var productoOpt = inventoryFacade.findProductByName(nombre);
                        if (productoOpt.isPresent()) {
                            carrito.agregarItem(productoOpt.get(), cantidad);
                        }
                    }
                    
                    // Restaurar descuento
                    String descuentoStr = carritoJson.split("\"descuento\":")[1].split("}")[0];
                    ventaDTO.setDescuento(Double.parseDouble(descuentoStr));
                    
                    mostrarMensaje(FacesMessage.SEVERITY_INFO, "Info", "Carrito restaurado con " + carrito.getItems().size() + " productos");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restaurar el carrito");
        }
    }
    
    private String extraerValor(String json, String clave) {
        String pattern = "\"" + clave + "\":\"?([^,}\\\"]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    // ===== Helpers privados (sin lógica de negocio, solo orquestación de UI) =====

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
    // Nombres descriptivos en español, consistentes con el dominio

    public Integer getProductoId() {
        return ventaDTO.getProductoSeleccionado() != null 
            ? ventaDTO.getProductoSeleccionado().getIdProduct() 
            : null;
    }

    public void setProductoId(Integer idProducto) {
        Product producto = (idProducto == null)
                ? null
                : inventoryFacade.findProductById(idProducto).orElse(null);
        ventaDTO.setProductoSeleccionado(producto);
    }

    public int getCantidad() {
        return ventaDTO.getCantidadSeleccionada();
    }

    public void setCantidad(int cantidad) {
        ventaDTO.setCantidadSeleccionada(cantidad);
    }

    public Long getClienteId() {
        return ventaDTO.getClienteSeleccionado() != null 
            ? ventaDTO.getClienteSeleccionado().getIdCustomer() 
            : null;
    }

    public void setClienteId(Long idCliente) {
        Customer cliente = (idCliente == null)
                ? null
                : inventoryFacade.findCustomerById(idCliente).orElse(null);
        ventaDTO.setClienteSeleccionado(cliente);
    }

    public String getMetodoPago() {
        return ventaDTO.getMetodoPago();
    }

    public void setMetodoPago(String metodoPago) {
        ventaDTO.setMetodoPago(metodoPago);
    }

    public double getDescuento() {
        return ventaDTO.getDescuento();
    }

    public void setDescuento(double descuento) {
        ventaDTO.setDescuento(descuento);
    }

    public List<ItemCarrito> getItemsCarrito() {
        return carrito.getItems();
    }

    public List<SaleInvoice> getFacturas() {
        return facturas;
    }

    public String getOrdenFacturas() {
        return ordenFacturas;
    }

    public void setOrdenFacturas(String ordenFacturas) {
        this.ordenFacturas = ordenFacturas;
        this.facturas = ventaService.obtenerFacturas(ordenFacturas);
    }

    public void cambiarOrdenFacturas() {
        if ("recientes".equals(ordenFacturas)) {
            ordenFacturas = "antiguos";
        } else {
            ordenFacturas = "recientes";
        }
        this.facturas = ventaService.obtenerFacturas(ordenFacturas);
    }

    public double getSubtotal() {
        return carrito.calcularSubtotal();
    }

    public double getIva() {
        return carrito.calcularIva();
    }

    public double getTotal() {
        return carrito.calcularTotal(ventaDTO.getDescuento());
    }

    public List<Product> getProductos() {
        return inventoryFacade.getAllProducts();
    }

    public List<Customer> getClientes() {
        return inventoryFacade.getAllCustomers();
    }
}