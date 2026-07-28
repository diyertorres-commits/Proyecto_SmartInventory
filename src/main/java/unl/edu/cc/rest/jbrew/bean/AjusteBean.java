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

    private Product selectedProduct;
    private String tipoAjuste;
    private int cantidad;
    private String operacion;
    private String motivo;
    private String responsable;

    private String mensajeStockBajo;

    public AjusteBean() {
        this.selectedProduct = new Product();
    }

    public String registrarAjuste() {
        if (selectedProduct == null || tipoAjuste == null || cantidad <= 0 || operacion == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Por favor complete todos los campos requeridos"));
            return null;
        }

        int stockAnterior = selectedProduct.getStock();
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

        selectedProduct.setStock(stockNuevo);
        inventoryService.saveProduct(selectedProduct);

        ajusteService.registrarAjuste(
            selectedProduct.getName(),
            tipoAjuste,
            operacion,
            cantidad,
            stockAnterior,
            stockNuevo,
            motivo != null ? motivo : "",
            responsable != null ? responsable : "No especificado"
        );

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Ajuste registrado correctamente"));

        if (selectedProduct.verifyStockMinimo()) {
            mensajeStockBajo = "Advertencia: poco stock del producto \"" + selectedProduct.getName()
                + "\" (quedan " + stockNuevo + " unidades, mínimo recomendado: " + selectedProduct.getMinStock() + ")";
            PrimeFaces.current().executeScript("PF('dlgStockBajo').show()");
        }

        limpiarCampos();
        return null;
    }

    public String getMensajeStockBajo() {
        return mensajeStockBajo;
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
        selectedProduct = null;
        tipoAjuste = null;
        cantidad = 0;
        operacion = null;
        motivo = null;
        responsable = null;
    }

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

    // Getters and Setters
    public Product getSelectedProduct() {
        return selectedProduct;
    }
    
    public Product getProducto() {
        return getSelectedProduct();
    }
    
    public int getProductoId() {
        return selectedProduct != null ? selectedProduct.getIdProduct() : 0;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }
    
    public void setProducto(Product selectedProduct) {
        setSelectedProduct(selectedProduct);
    }
    
    public void setProductoId(int productId) {
        Product product = inventoryService.findProductById(productId).orElse(null);
        setSelectedProduct(product);
    }

    public List<Product> getAvailableProducts() {
        return inventoryService.getAllProducts();
    }
    
    public List<Product> getProductos() {
        return getAvailableProducts();
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
        return ajusteService.obtenerAjustes();
    }
}
