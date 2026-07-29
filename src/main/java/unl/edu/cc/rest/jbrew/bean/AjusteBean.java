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

    // Id del producto elegido en el combo. Integer (no int) para poder
    // representar "sin selección" como null.
    private Integer productoId;

    private String tipoAjuste;
    private int cantidadAjuste;
    private String tipoOperacion; // "restar" o "sumar"
    private String observacion;
    private String responsable;

    private String mensajeStockBajo;

    public String registrarAjuste() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (productoId == null || tipoAjuste == null || tipoOperacion == null || cantidadAjuste <= 0) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Advertencia", "Por favor complete todos los campos requeridos"));
            return null;
        }

        Product producto = inventoryService.findProductById(productoId).orElse(null);
        if (producto == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "El producto seleccionado ya no existe"));
            return null;
        }

        int stockAnterior = producto.getStock();
        int stockNuevo;

        if ("restar".equals(tipoOperacion)) {
            if (stockAnterior < cantidadAjuste) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error", "Stock insuficiente para restar"));
                return null;
            }
            stockNuevo = stockAnterior - cantidadAjuste;
        } else {
            stockNuevo = stockAnterior + cantidadAjuste;
        }

        producto.setStock(stockNuevo);
        inventoryService.saveProduct(producto);

        ajusteService.registrarAjuste(
                producto.getName(),
                tipoAjuste,
                tipoOperacion,
                cantidadAjuste,
                stockAnterior,
                stockNuevo,
                observacion != null ? observacion : "",
                responsable != null ? responsable : "No especificado"
        );

        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Éxito", "Ajuste registrado correctamente"));

        if (producto.verifyStockMinimo()) {
            mensajeStockBajo = "Advertencia: poco stock del producto \"" + producto.getName()
                    + "\" (quedan " + stockNuevo + " unidades, mínimo recomendado: " + producto.getMinStock() + ")";
            PrimeFaces.current().executeScript("PF('dlgStockBajo').show()");
        }

        limpiarCampos();
        return null;
    }

    public void revertir(Ajuste ajuste) {
        Product producto = inventoryService.findProductByName(ajuste.getProductoNombre()).orElse(null);
        if (producto != null) {
            producto.setStock(ajuste.getStockAnterior());
            inventoryService.saveProduct(producto);
            ajusteService.eliminarAjuste(ajuste);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ajuste revertido correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo revertir el ajuste"));
        }
    }

    private void limpiarCampos() {
        productoId = null;
        tipoAjuste = null;
        cantidadAjuste = 0;
        tipoOperacion = null;
        observacion = null;
        responsable = null;
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

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public List<Product> getProductos() {
        return inventoryService.getAllProducts();
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

    public String getMensajeStockBajo() {
        return mensajeStockBajo;
    }

    public List<Ajuste> getAjustes() {
        return ajusteService.obtenerAjustes();
    }
}