package unl.edu.cc.rest.jbrew.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ajustes")
public class Ajuste implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int idAjuste;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "producto_nombre", nullable = false)
    private String productoNombre;

    @Column(name = "tipo_ajuste", nullable = false)
    private String tipoAjuste;

    @Column(name = "operacion", nullable = false)
    private String operacion;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private int stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private int stockNuevo;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "responsable", nullable = false)
    private String responsable;

    public Ajuste() {
    }

    public Ajuste(int idAjuste, Date fecha, String productoNombre, String tipoAjuste, String operacion,
                  int cantidad, int stockAnterior, int stockNuevo, String motivo, String responsable) {
        this.idAjuste = idAjuste;
        this.fecha = fecha;
        this.productoNombre = productoNombre;
        this.tipoAjuste = tipoAjuste;
        this.operacion = operacion;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.motivo = motivo;
        this.responsable = responsable;
    }

    public String getFechaTexto() {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
    }

    public String getTipoCss() {
        switch (tipoAjuste) {
            case "merma":
            case "robo":
            case "dano":
            case "vencimiento":
                return "status-agotado";
            case "error":
            case "manual":
                return "status-bajo";
            default:
                return "status-disponible";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdAjuste() {
        return idAjuste;
    }

    public void setIdAjuste(int idAjuste) {
        this.idAjuste = idAjuste;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(String tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(int stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public int getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(int stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
