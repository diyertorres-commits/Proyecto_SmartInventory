package unl.edu.cc.rest.jbrew.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.business.ReporteService;
import unl.edu.cc.rest.jbrew.business.ReportExportService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;

@Named
@ViewScoped
public class ReporteBean implements Serializable {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private ReporteService reporteService;

    @Inject
    private ReportExportService reportExportService;

    private String tipoReporte;
    private Date fechaInicio;
    private Date fechaFin;
    private Category category;

    private double totalVentas;
    private double ganancia;
    private int totalTransacciones;
    private double margenPromedio;

    private List<ReporteService.DatoReporte> datosReporte;

    private Object chartVentasCategoria;
    private Object chartTendenciaVentas;
    private Object chartMetodosPago;

    public ReporteBean() {
        this.tipoReporte = "ventas";
        this.datosReporte = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        generarReporte();
    }

    public void generarReporte() {
        datosReporte = new ArrayList<>();
        totalVentas = 0;
        ganancia = 0;
        totalTransacciones = 0;
        margenPromedio = 0;

        ReporteService.ReporteData resultado;
        
        if ("ventas".equals(tipoReporte)) {
            resultado = reporteService.generarReporteVentas();
        } else if ("compras".equals(tipoReporte)) {
            resultado = reporteService.generarReporteCompras();
        } else if ("rotacion".equals(tipoReporte)) {
            resultado = reporteService.generarReporteRotacion();
        } else if ("stockBajo".equals(tipoReporte)) {
            resultado = reporteService.generarReporteStockBajo();
        } else if ("ganancias".equals(tipoReporte)) {
            resultado = reporteService.generarReporteGanancias();
        } else if ("ventasCliente".equals(tipoReporte)) {
            resultado = reporteService.generarReporteVentasCliente();
        } else {
            resultado = ReporteService.ReporteData.error("Tipo de reporte no válido");
        }

        if (resultado.isExitoso()) {
            datosReporte = resultado.getDatos();
            totalVentas = resultado.getTotalVentas();
            ganancia = resultado.getGanancia();
            totalTransacciones = resultado.getTotalTransacciones();
            margenPromedio = resultado.getMargenPromedio();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Reporte Generado", resultado.getMensaje()));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Datos", resultado.getMensaje()));
        }
    }

    public void generar() {
        generarReporte();
    }


    public void exportarPDF() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
            reportExportService.exportPDF(datosReporte, tipoReporte, totalVentas, ganancia, totalTransacciones, margenPromedio, response);
            
            facesContext.responseComplete();
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exportación Exitosa", "El reporte PDF se ha generado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al exportar PDF", "Error: " + e.getMessage()));
        }
    }

    public void exportarExcel() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
            reportExportService.exportExcel(datosReporte, tipoReporte, totalVentas, ganancia, totalTransacciones, margenPromedio, response);
            
            facesContext.responseComplete();
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exportación Exitosa", "El reporte Excel se ha generado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al exportar Excel", "Error: " + e.getMessage()));
        }
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

    public List<ReporteService.DatoReporte> getDatosReporte() {
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

}