package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.SalesService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class ReporteBean implements Serializable {
    
    @Inject
    private InventoryFacade inventoryFacade;
    
    @Inject
    private SalesService salesService;
    
    @Inject
    private PurchaseService purchaseService;
    
    private String tipoReporte;
    private Date fechaInicio;
    private Date fechaFin;
    private String categoria;
    
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
        
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Reporte Generado", "El reporte se ha generado correctamente"));
    }
    
    public void generar() {
        generarReporte();
    }
    
    private void generarReporteVentas() {
        for (SaleInvoice factura : salesService.getSaleInvoices()) {
            DatoReporte dato = new DatoReporte();
            dato.setId(factura.getIdInvoice());
            dato.setFecha(factura.getInvoiceDate());
            dato.setTipo(factura.getInvoiceNumber());
            dato.setCliente(factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador");
            dato.setMetodo(factura.getPaymentMethod());
            dato.setTotal(0);
            datosReporte.add(dato);
            
            totalTransacciones++;
        }
    }
    
    private void generarReporteCompras() {
        for (PurchaseInvoice factura : purchaseService.getPurchaseInvoices()) {
            DatoReporte dato = new DatoReporte();
            dato.setId(factura.getIdInvoice());
            dato.setFecha(factura.getInvoiceDate());
            dato.setTipo(factura.getInvoiceNumber());
            dato.setCliente(factura.getSupplier() != null ? factura.getSupplier().getName() : "N/A");
            dato.setMetodo(factura.getPurchaseOrderNumber());
            dato.setTotal(0);
            datosReporte.add(dato);
            
            totalTransacciones++;
        }
    }
    
    private void generarReporteRotacion() {
        for (Product producto : inventoryFacade.getAllProducts()) {
            DatoReporte dato = new DatoReporte();
            dato.setId(producto.getIdProduct());
            dato.setTipo(producto.getName());
            dato.setCliente(producto.getCategoria());
            dato.setMetodo(String.valueOf(producto.getStock()));
            dato.setTotal(producto.getSalePrice());
            datosReporte.add(dato);
        }
        
        totalTransacciones = inventoryFacade.getAllProducts().size();
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
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public double getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }
    
    public double getGanancia() {
        return ganancia;
    }
    
    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }
    
    public int getTotalTransacciones() {
        return totalTransacciones;
    }
    
    public void setTotalTransacciones(int totalTransacciones) {
        this.totalTransacciones = totalTransacciones;
    }
    
    public double getMargenPromedio() {
        return margenPromedio;
    }
    
    public void setMargenPromedio(double margenPromedio) {
        this.margenPromedio = margenPromedio;
    }
    
    public List<DatoReporte> getDatosReporte() {
        return datosReporte;
    }
    
    public void setDatosReporte(List<DatoReporte> datosReporte) {
        this.datosReporte = datosReporte;
    }
    
    public Object getChartVentasCategoria() {
        return chartVentasCategoria;
    }
    
    public void setChartVentasCategoria(Object chartVentasCategoria) {
        this.chartVentasCategoria = chartVentasCategoria;
    }
    
    public Object getChartTendenciaVentas() {
        return chartTendenciaVentas;
    }
    
    public void setChartTendenciaVentas(Object chartTendenciaVentas) {
        this.chartTendenciaVentas = chartTendenciaVentas;
    }
    
    public Object getChartMetodosPago() {
        return chartMetodosPago;
    }
    
    public void setChartMetodosPago(Object chartMetodosPago) {
        this.chartMetodosPago = chartMetodosPago;
    }
    
    // Clase interna para datos del reporte
    public static class DatoReporte implements Serializable {
        private int id;
        private Date fecha;
        private String tipo;
        private String cliente;
        private String metodo;
        private double total;
        
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
        
        public String getCliente() {
            return cliente;
        }
        
        public void setCliente(String cliente) {
            this.cliente = cliente;
        }
        
        public String getMetodo() {
            return metodo;
        }
        
        public void setMetodo(String metodo) {
            this.metodo = metodo;
        }
        
        public double getTotal() {
            return total;
        }
        
        public void setTotal(double total) {
            this.total = total;
        }
    }
}
