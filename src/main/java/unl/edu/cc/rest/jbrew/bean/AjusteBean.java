package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import unl.edu.cc.rest.jbrew.business.AjusteService;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.domain.Ajuste;
import unl.edu.cc.rest.jbrew.domain.AjusteRequest;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AjusteBean implements Serializable {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private AjusteService ajusteService;

    private AjusteRequest ajusteRequest = new AjusteRequest();
    private String mensajeStockBajo;

    public String registrarAjuste() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        AjusteService.AjusteResult resultado = ajusteService.procesarAjuste(ajusteRequest);
        
        if (!resultado.isExitoso()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", resultado.getMensaje()));
            return null;
        }

        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", resultado.getMensaje()));

        if (resultado.isStockBajo()) {
            mensajeStockBajo = resultado.getMensajeStockBajo();
            PrimeFaces.current().executeScript("PF('dlgStockBajo').show()");
        }

        limpiarCampos();
        return null;
    }

    public void revertir(Ajuste ajuste) {
        ajusteService.revertirAjuste(ajuste);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ajuste revertido correctamente"));
    }

    private void limpiarCampos() {
        ajusteRequest = new AjusteRequest();
    }

    // ---------- Estadísticas ----------

    public int getTotalRestado() {
        return ajusteService.obtenerAjustes().stream()
                .filter(a -> "restar".equals(a.getOperacion()))
                .mapToInt(Ajuste::getCantidad)
                .sum();
    }

    public int getTotalSumado() {
        return ajusteService.obtenerAjustes().stream()
                .filter(a -> "sumar".equals(a.getOperacion()))
                .mapToInt(Ajuste::getCantidad)
                .sum();
    }

    public int getTotalAjustes() {
        return ajusteService.obtenerAjustes().size();
    }

    // ---------- Getters y Setters ----------

    public AjusteRequest getAjusteRequest() {
        return ajusteRequest;
    }

    public void setAjusteRequest(AjusteRequest ajusteRequest) {
        this.ajusteRequest = ajusteRequest;
    }

    // Getters para compatibilidad con vistas existentes
    public Integer getProductoId() {
        return ajusteRequest.getProductoId();
    }

    public void setProductoId(Integer productoId) {
        ajusteRequest.setProductoId(productoId);
    }

    public String getTipoAjuste() {
        return ajusteRequest.getTipoAjuste();
    }

    public void setTipoAjuste(String tipoAjuste) {
        ajusteRequest.setTipoAjuste(tipoAjuste);
    }

    public int getCantidadAjuste() {
        return ajusteRequest.getCantidadAjuste();
    }

    public void setCantidadAjuste(int cantidadAjuste) {
        ajusteRequest.setCantidadAjuste(cantidadAjuste);
    }

    public String getTipoOperacion() {
        return ajusteRequest.getTipoOperacion();
    }

    public void setTipoOperacion(String tipoOperacion) {
        ajusteRequest.setTipoOperacion(tipoOperacion);
    }

    public String getObservacion() {
        return ajusteRequest.getObservacion();
    }

    public void setObservacion(String observacion) {
        ajusteRequest.setObservacion(observacion);
    }

    public String getResponsable() {
        return ajusteRequest.getResponsable();
    }

    public void setResponsable(String responsable) {
        ajusteRequest.setResponsable(responsable);
    }

    public List<Product> getProductos() {
        return inventoryService.getAllProducts();
    }

    public String getMensajeStockBajo() {
        return mensajeStockBajo;
    }

    public List<Ajuste> getAjustes() {
        return ajusteService.obtenerAjustes();
    }
}