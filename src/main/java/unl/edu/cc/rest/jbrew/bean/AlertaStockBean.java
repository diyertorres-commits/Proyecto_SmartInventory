package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;
import java.util.List;

@Named
@jakarta.enterprise.context.SessionScoped
public class AlertaStockBean implements Serializable {

    @Inject
    private InventoryService inventoryService;

    private List<Product> productosCriticos;
    private int cantidadAlertas;
    private boolean mostrarBanner;
    private boolean bannerCerrado;

    public AlertaStockBean() {
        this.productosCriticos = List.of();
        this.cantidadAlertas = 0;
        this.mostrarBanner = true;
        this.bannerCerrado = false;
    }

    public void verificarStockBajo() {
        productosCriticos = inventoryService.findProductsWithCriticalStock();
        cantidadAlertas = productosCriticos.size();
        
        // Verificar si el usuario ya cerró el banner hoy usando localStorage
        String ultimaFechaCierre = (String) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get("ultimaFechaCierreBanner");
        
        String hoy = java.time.LocalDate.now().toString();
        if (hoy.equals(ultimaFechaCierre)) {
            mostrarBanner = false;
        } else {
            mostrarBanner = productosCriticos.size() > 0;
        }
    }

    public void cerrarBanner() {
        bannerCerrado = true;
        mostrarBanner = false;
        
        // Guardar fecha de cierre en session
        String hoy = java.time.LocalDate.now().toString();
        
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put("ultimaFechaCierreBanner", hoy);
    }

    // Getters

    public List<Product> getProductosCriticos() {
        verificarStockBajo();
        return productosCriticos;
    }

    public int getCantidadAlertas() {
        verificarStockBajo();
        return cantidadAlertas;
    }

    public String getBadgeStyleClass() {
        return getCantidadAlertas() > 0 ? "si-menu-badge" : "";
    }

    public String getBadgeInlineStyle() {
        int cantidad = getCantidadAlertas();
        return cantidad > 0 ? "--badge-count: '" + cantidad + "';" : "";
    }

    public boolean isMostrarBanner() {
        return mostrarBanner;
    }

    public boolean isBannerCerrado() {
        return bannerCerrado;
    }
}