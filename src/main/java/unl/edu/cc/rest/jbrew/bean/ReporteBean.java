package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.business.VentaService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.ProductMovement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PENDIENTE DE REVISAR:
 *
 * - fechaInicio / fechaFin / category se capturan pero NUNCA se usan
 *   para filtrar los reportes. Falta aplicar el filtro en cada método
 *   generarReporte*().
 * - "stockBajo", "ganancias" y "ventasCliente" aparecen como opciones en
 *   el combo de reportes.xhtml pero generarReporte() no los maneja: al
 *   elegirlos, "Generar Reporte" no hace nada.
 * - dato.getGanancia() siempre devuelve 0: calcular la ganancia real
 *   (precio de venta - costo) requiere saber si Product tiene un campo
 *   de precio de costo. Falta confirmar Product.java.
 * - generarReporteCompras() sigue reportando por factura completa (no
 *   por línea de producto) porque no tengo la estructura de
 *   PurchaseInvoice/Movement de compra todavía.
 */
@Named
@ViewScoped
public class ReporteBean implements Serializable {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private VentaService ventaService;

    @Inject
    private PurchaseService purchaseService;

    private String tipoReporte;
    private Date fechaInicio;
    private Date fechaFin;
    private Category category;

    private double totalVentas;
    private double ganancia;
    private int totalTransacciones;
    private double margenPromedio;

    private List<DatoReporte> datosReporte;

    private Object chartVentasCategoria;
    private Object chartTendenciaVentas;
    private Object chartMetodosPago;

    public ReporteBean() {
        this.tipoReporte = "ventas";
        this.datosReporte = new ArrayList<>();
    }

    public void generarReporte() {
        datosReporte = new ArrayList<>();
        totalVentas = 0;
        ganancia = 0;
        totalTransacciones = 0;
        margenPromedio = 0;

        if ("ventas".equals(tipoReporte)) {
            generarReporteVentas();
        } else if ("compras".equals(tipoReporte)) {
            generarReporteCompras();
        } else if ("rotacion".equals(tipoReporte)) {
            generarReporteRotacion();
        }
        // TODO: falta implementar "stockBajo", "ganancias" y "ventasCliente",
        // que ya aparecen como opciones en el combo del XHTML.

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Reporte Generado", "El reporte se ha generado correctamente"));
    }

    public void generar() {
        generarReporte();
    }

    /**
     * FIX: antes se generaba UNA fila por factura completa, usando
     * propiedades que DatoReporte ni siquiera tenía (descripcion,
     * cantidad, precioUnitario, tercero, ganancia -> todas lanzaban
     * PropertyNotFoundException al renderizar la tabla).
     *
     * Ahora se genera una fila POR LÍNEA DE PRODUCTO vendido (a través
     * de factura.getMovement().getProductMovementList()), que es lo que
     * las columnas "Producto/Servicio", "Cantidad" y "Precio Unitario"
     * realmente esperan.
     */
    private void generarReporteVentas() {
        for (SaleInvoice factura : ventaService.obtenerFacturas()) {
            String tercero = factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador";

            if (factura.getMovement() == null) {
                continue; // factura sin movimiento asociado (no debería pasar, pero por seguridad)
            }

            for (ProductMovement linea : factura.getMovement().getProductMovementList()) {
                DatoReporte dato = new DatoReporte();
                dato.setFecha(factura.getInvoiceDate());
                dato.setDescripcion(linea.getProduct().getName());
                dato.setCantidad(linea.getQuantity());
                dato.setPrecioUnitario(linea.getUnitPrice());
                dato.setTotal(linea.getSubtotal());
                dato.setTercero(tercero);
                dato.setMetodo(factura.getPaymentMethod());
                dato.setGanancia(0); // TODO: requiere precio de costo en Product
                datosReporte.add(dato);

                totalVentas += linea.getSubtotal();
            }
            totalTransacciones++;
        }
    }

    private void generarReporteCompras() {
        // TODO: idealmente esto también debería desglosarse por línea de
        // producto comprado, igual que se hizo en generarReporteVentas,
        // en cuanto se comparta la estructura de PurchaseInvoice/Movement.
        for (PurchaseInvoice factura : purchaseService.getPurchaseInvoices()) {
            DatoReporte dato = new DatoReporte();
            dato.setFecha(factura.getInvoiceDate());
            dato.setDescripcion(factura.getInvoiceNumber());
            dato.setCantidad(0);
            dato.setPrecioUnitario(0);
            dato.setTotal(factura.getTotal());
            dato.setTercero(factura.getSupplier() != null ? factura.getSupplier().getName() : "N/A");
            dato.setMetodo(factura.getPurchaseOrderNumber());
            dato.setGanancia(0);
            datosReporte.add(dato);

            totalTransacciones++;
        }
    }

    private void generarReporteRotacion() {
        for (Product producto : inventoryService.getAllProducts()) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(producto.getName());
            dato.setCantidad(producto.getStock());
            dato.setPrecioUnitario(producto.getSalePrice());
            dato.setTotal(producto.getStock() * producto.getSalePrice());
            dato.setTercero(producto.getCategory() != null ? producto.getCategory().getName() : "");
            dato.setMetodo("");
            dato.setGanancia(0);
            datosReporte.add(dato);
        }

        totalTransacciones = inventoryService.getAllProducts().size();
    }

    public void exportarPDF() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Exportar PDF", "Función de exportación PDF pendiente de implementar"));
    }

    public void exportarExcel() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Exportar Excel", "Función de exportación Excel pendiente de implementar"));
    }

    // Getters y Setters
    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    public void setCategoryName(String categoryName) {
        if (categoryName != null) {
            this.category = inventoryService.getAllCategories().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElse(null);
        } else {
            this.category = null;
        }
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public double getGanancia() {
        return ganancia;
    }

    public int getTotalTransacciones() {
        return totalTransacciones;
    }

    public double getMargenPromedio() {
        return margenPromedio;
    }

    public List<DatoReporte> getDatosReporte() {
        return datosReporte;
    }

    public Object getChartVentasCategoria() {
        return chartVentasCategoria;
    }

    public Object getChartTendenciaVentas() {
        return chartTendenciaVentas;
    }

    public Object getChartMetodosPago() {
        return chartMetodosPago;
    }

    /**
     * FIX: antes tenía (id, fecha, tipo, cliente, metodo, total) — ni
     * remotamente lo que reportes.xhtml pide. Ahora expone exactamente
     * las columnas que usa la tabla: fecha, descripcion, cantidad,
     * precioUnitario, total, tercero, metodo, ganancia.
     */
    public static class DatoReporte implements Serializable {
        private Date fecha;
        private String descripcion;
        private int cantidad;
        private double precioUnitario;
        private double total;
        private String tercero;
        private String metodo;
        private double ganancia;

        public Date getFecha() {
            return fecha;
        }

        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getTercero() {
            return tercero;
        }

        public void setTercero(String tercero) {
            this.tercero = tercero;
        }

        public String getMetodo() {
            return metodo;
        }

        public void setMetodo(String metodo) {
            this.metodo = metodo;
        }

        public double getGanancia() {
            return ganancia;
        }

        public void setGanancia(double ganancia) {
            this.ganancia = ganancia;
        }
    }
}