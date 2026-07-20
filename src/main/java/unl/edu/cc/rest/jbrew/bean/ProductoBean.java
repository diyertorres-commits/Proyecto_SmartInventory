package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Inventory.ProductStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class ProductoBean implements Serializable {
    
    @Inject
    private InventarioBean inventarioBean;
    
    private Product producto;
    private List<Product> productosFiltrados;
    private String searchTerm;
    private String filtroCategoria;
    private String filtroEstado;
    
    private int stockTotal;
    private int stockBajo;
    private int stockDisponible;
    
    public ProductoBean() {
        this.producto = new Product();
        this.productosFiltrados = new ArrayList<>();
        this.searchTerm = "";
        this.filtroCategoria = null;
        this.filtroEstado = null;
    }
    
    public void prepararNuevo() {
        this.producto = new Product();
    }
    
    public void editar(Product producto) {
        this.producto = producto;
    }
    
    public String guardar() {
        try {
            if (producto.getIdProduct() == 0) {
                // Nuevo producto
                producto.setIdProduct(inventarioBean.getProductos().size() + 1);
                inventarioBean.getProductos().add(producto);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto creado correctamente"));
            } else {
                // Editar producto existente
                for (int i = 0; i < inventarioBean.getProductos().size(); i++) {
                    if (inventarioBean.getProductos().get(i).getIdProduct() == producto.getIdProduct()) {
                        inventarioBean.getProductos().set(i, producto);
                        break;
                    }
                }
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente"));
            }
            prepararNuevo();
            actualizarEstadisticas();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar producto: " + e.getMessage()));
            return null;
        }
    }
    
    public void eliminar(Product producto) {
        try {
            inventarioBean.getProductos().remove(producto);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado correctamente"));
            actualizarEstadisticas();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar producto: " + e.getMessage()));
        }
    }
    
    public void search() {
        productosFiltrados = new ArrayList<>();
        for (Product p : inventarioBean.getProductos()) {
            if (searchTerm == null || searchTerm.isEmpty() || 
                p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                p.getCodigo().toLowerCase().contains(searchTerm.toLowerCase())) {
                productosFiltrados.add(p);
            }
        }
        aplicarFiltros();
    }
    
    public void filter() {
        search();
    }
    
    public void clearFilters() {
        searchTerm = "";
        filtroCategoria = null;
        filtroEstado = null;
        productosFiltrados = new ArrayList<>(inventarioBean.getProductos());
    }
    
    private void aplicarFiltros() {
        List<Product> filtrados = new ArrayList<>(productosFiltrados);
        
        if (filtroCategoria != null && !filtroCategoria.isEmpty()) {
            filtrados.removeIf(p -> !filtroCategoria.equals(p.getCategoria()));
        }
        
        if (filtroEstado != null && !filtroEstado.isEmpty()) {
            filtrados.removeIf(p -> !filtroEstado.equals(getEstadoTexto(p.getEstado())));
        }
        
        productosFiltrados = filtrados;
    }
    
    private String getEstadoTexto(ProductStatus estado) {
        if (estado == ProductStatus.DISPONIBLE) return "disponible";
        if (estado == ProductStatus.STOCK_BAJO) return "bajo";
        if (estado == ProductStatus.AGOTADO) return "agotado";
        return "";
    }
    
    private void actualizarEstadisticas() {
        stockTotal = 0;
        stockBajo = 0;
        stockDisponible = 0;
        
        for (Product p : inventarioBean.getProductos()) {
            stockTotal += p.getStock();
            if (p.getStock() <= p.getMinStock()) {
                stockBajo++;
            }
            if (p.getStock() > p.getMinStock()) {
                stockDisponible++;
            }
        }
    }
    
    // Getters y Setters
    public Product getProducto() {
        return producto;
    }
    
    public void setProducto(Product producto) {
        this.producto = producto;
    }
    
    public List<Product> getProductosFiltrados() {
        if (productosFiltrados.isEmpty()) {
            productosFiltrados = new ArrayList<>(inventarioBean.getProductos());
            actualizarEstadisticas();
        }
        return productosFiltrados;
    }
    
    public void setProductosFiltrados(List<Product> productosFiltrados) {
        this.productosFiltrados = productosFiltrados;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getFiltroCategoria() {
        return filtroCategoria;
    }
    
    public void setFiltroCategoria(String filtroCategoria) {
        this.filtroCategoria = filtroCategoria;
    }
    
    public String getFiltroEstado() {
        return filtroEstado;
    }
    
    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }
    
    public int getStockTotal() {
        return stockTotal;
    }
    
    public void setStockTotal(int stockTotal) {
        this.stockTotal = stockTotal;
    }
    
    public int getStockBajo() {
        return stockBajo;
    }
    
    public void setStockBajo(int stockBajo) {
        this.stockBajo = stockBajo;
    }
    
    public int getStockDisponible() {
        return stockDisponible;
    }
    
    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
    }
}
