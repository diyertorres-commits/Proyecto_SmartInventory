package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InvoiceService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class FacturaBean implements Serializable {

    @Inject
    private InvoiceService invoiceService;

    private List<InvoiceService.InvoiceInfo> facturasFiltradas = new ArrayList<>();
    private InvoiceService.InvoiceInfo facturaSeleccionada = new InvoiceService.InvoiceInfo();

    // Filtros de búsqueda
    private InvoiceService.InvoiceFilter filtro = new InvoiceService.InvoiceFilter();

    public void filtrar() {
        facturasFiltradas = invoiceService.filtrarFacturas(filtro);
    }

    public void limpiarFiltros() {
        filtro = new InvoiceService.InvoiceFilter();
        filtrar();
    }

    public void verDetalle(InvoiceService.InvoiceInfo factura) {
        this.facturaSeleccionada = factura;
    }

    public String getFiltroTipo() {
        return filtro.getTipo();
    }

    public void setFiltroTipo(String filtroTipo) {
        filtro.setTipo(filtroTipo);
    }

    public String getFiltroCodigo() {
        return filtro.getCodigo();
    }

    public void setFiltroCodigo(String filtroCodigo) {
        filtro.setCodigo(filtroCodigo);
    }

    public Date getFiltroFechaDesde() {
        return filtro.getFechaDesde();
    }

    public void setFiltroFechaDesde(Date filtroFechaDesde) {
        filtro.setFechaDesde(filtroFechaDesde);
    }

    public Date getFiltroFechaHasta() {
        return filtro.getFechaHasta();
    }

    public void setFiltroFechaHasta(Date filtroFechaHasta) {
        filtro.setFechaHasta(filtroFechaHasta);
    }

    public Integer getFiltroMes() {
        return filtro.getMes();
    }

    public void setFiltroMes(Integer filtroMes) {
        filtro.setMes(filtroMes);
    }

    public List<InvoiceService.InvoiceInfo> getFacturasFiltradas() {
        if (facturasFiltradas.isEmpty()) {
            filtrar();
        }
        return facturasFiltradas;
    }

    public InvoiceService.InvoiceInfo getFacturaSeleccionada() {
        return facturaSeleccionada;
    }
}