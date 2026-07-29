package unl.edu.cc.rest.jbrew.domain;

import java.io.Serializable;

public class AjusteRequest implements Serializable {

    private Integer productoId;
    private String tipoAjuste;
    private int cantidadAjuste;
    private String tipoOperacion;
    private String observacion;
    private String responsable;

    public AjusteRequest() {
        this.cantidadAjuste = 0;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(String tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public int getCantidadAjuste() {
        return cantidadAjuste;
    }

    public void setCantidadAjuste(int cantidadAjuste) {
        this.cantidadAjuste = cantidadAjuste;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
