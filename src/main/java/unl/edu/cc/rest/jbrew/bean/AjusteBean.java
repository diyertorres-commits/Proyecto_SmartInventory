package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class AjusteBean implements Serializable {

    private InventarioBean inventarioBean;

    private int productoId;
    private String tipoAjuste;
    private int cantidad;
    private String operacion;
    private String motivo;
    private String responsable;

    private List<Ajuste> ajustes;
    private int contadorAjustes;

    public AjusteBean() {
        // Default constructor required for CDI
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        inventarioBean = new InventarioBean();
        ajustes = new ArrayList<>();
        contadorAjustes = 1;
    }

    public String registrarAjuste() {
        if (productoId == 0 || tipoAjuste == null || cantidad <= 0 || operacion == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Por favor complete todos los campos requeridos"));
            return null;
        }

        Product producto = inventarioBean.buscarProductoPorId(productoId);
        if (producto == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Producto no encontrado"));
            return null;
        }

        int stockAnterior = producto.getStock();
        int stockNuevo = stockAnterior;

        if ("restar".equals(operacion)) {
            if (stockAnterior < cantidad) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Stock insuficiente para restar"));
                return null;
            }
            stockNuevo = stockAnterior - cantidad;
        } else {
            stockNuevo = stockAnterior + cantidad;
        }

        producto.setStock(stockNuevo);

        Ajuste ajuste = new Ajuste(
            contadorAjustes++,
            new Date(),
            producto.getName(),
            tipoAjuste,
            operacion,
            cantidad,
            stockAnterior,
            stockNuevo,
            motivo != null ? motivo : "",
            responsable != null ? responsable : "No especificado"
        );

        ajustes.add(ajuste);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ajuste registrado correctamente"));

        limpiarCampos();
        return null;
    }

    public void revertir(Ajuste ajuste) {
        Product producto = inventarioBean.buscarProductoPorNombre(ajuste.getProductoNombre());
        if (producto != null) {
            producto.setStock(ajuste.getStockAnterior());
            ajustes.remove(ajuste);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ajuste revertido correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo revertir el ajuste"));
        }
    }

    private void limpiarCampos() {
        productoId = 0;
        tipoAjuste = null;
        cantidad = 0;
        operacion = null;
        motivo = null;
        responsable = null;
    }

    public int getTotalRestado() {
        return ajustes.stream()
            .filter(a -> "restar".equals(a.getOperacion()))
            .mapToInt(Ajuste::getCantidad)
            .sum();
    }

    public int getTotalSumado() {
        return ajustes.stream()
            .filter(a -> "sumar".equals(a.getOperacion()))
            .mapToInt(Ajuste::getCantidad)
            .sum();
    }

    public int getTotalAjustes() {
        return ajustes.size();
    }

    // Getters y Setters
    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(String tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
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

    public List<Ajuste> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<Ajuste> ajustes) {
        this.ajustes = ajustes;
    }

    public static class Ajuste implements Serializable {
        private int id;
        private Date fecha;
        private String productoNombre;
        private String tipoAjuste;
        private String operacion;
        private int cantidad;
        private int stockAnterior;
        private int stockNuevo;
        private String motivo;
        private String responsable;

        private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        public Ajuste(int id, Date fecha, String productoNombre, String tipoAjuste, String operacion,
                     int cantidad, int stockAnterior, int stockNuevo, String motivo, String responsable) {
            this.id = id;
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
            return sdf.format(fecha);
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

        // Getters
        public int getId() { return id; }
        public Date getFecha() { return fecha; }
        public String getProductoNombre() { return productoNombre; }
        public String getTipoAjuste() { return tipoAjuste; }
        public String getOperacion() { return operacion; }
        public int getCantidad() { return cantidad; }
        public int getStockAnterior() { return stockAnterior; }
        public int getStockNuevo() { return stockNuevo; }
        public String getMotivo() { return motivo; }
        public String getResponsable() { return responsable; }
    }
}
