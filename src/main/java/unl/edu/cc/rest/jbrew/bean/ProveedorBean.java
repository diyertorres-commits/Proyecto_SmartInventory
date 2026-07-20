package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class ProveedorBean implements Serializable {
    
    @Inject
    private InventarioBean inventarioBean;
    
    private Supplier proveedor;
    private List<Supplier> proveedores;
    private List<Supplier> proveedoresFiltrados;
    private String searchTerm;
    
    public ProveedorBean() {
        this.proveedor = new Supplier();
        this.proveedores = new ArrayList<>();
        this.proveedoresFiltrados = new ArrayList<>();
        this.searchTerm = "";
    }
    
    public void prepararNuevo() {
        this.proveedor = new Supplier();
    }
    
    public void editar(Supplier proveedor) {
        this.proveedor = proveedor;
    }
    
    public String guardar() {
        try {
            if (proveedor.getIdSupplier() == 0) {
                // Nuevo proveedor
                proveedor.setIdSupplier(inventarioBean.getProveedores().size() + 1);
                inventarioBean.getProveedores().add(proveedor);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Proveedor creado correctamente"));
            } else {
                // Editar proveedor existente
                for (int i = 0; i < inventarioBean.getProveedores().size(); i++) {
                    if (inventarioBean.getProveedores().get(i).getIdSupplier() == proveedor.getIdSupplier()) {
                        inventarioBean.getProveedores().set(i, proveedor);
                        break;
                    }
                }
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Proveedor actualizado correctamente"));
            }
            prepararNuevo();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar proveedor: " + e.getMessage()));
            return null;
        }
    }
    
    public void eliminar(Supplier proveedor) {
        try {
            inventarioBean.getProveedores().remove(proveedor);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Proveedor eliminado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar proveedor: " + e.getMessage()));
        }
    }
    
    // Getters y Setters
    public Supplier getProveedor() {
        return proveedor;
    }
    
    public void setProveedor(Supplier proveedor) {
        this.proveedor = proveedor;
    }
    
    public List<Supplier> getProveedores() {
        if (proveedores.isEmpty()) {
            proveedores = inventarioBean.getProveedores();
            proveedoresFiltrados = new ArrayList<>(proveedores);
        }
        return proveedores;
    }
    
    public List<Supplier> getProveedoresFiltrados() {
        if (proveedoresFiltrados.isEmpty()) {
            proveedoresFiltrados = new ArrayList<>(inventarioBean.getProveedores());
        }
        return proveedoresFiltrados;
    }
    
    public void setProveedoresFiltrados(List<Supplier> proveedoresFiltrados) {
        this.proveedoresFiltrados = proveedoresFiltrados;
    }
    
    public void search() {
        proveedoresFiltrados = new ArrayList<>();
        for (Supplier s : inventarioBean.getProveedores()) {
            if (searchTerm == null || searchTerm.isEmpty() || 
                s.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                s.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                s.getContacto().toLowerCase().contains(searchTerm.toLowerCase())) {
                proveedoresFiltrados.add(s);
            }
        }
    }
    
    public void clearSearch() {
        searchTerm = "";
        proveedoresFiltrados = new ArrayList<>(inventarioBean.getProveedores());
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public void setProveedores(List<Supplier> proveedores) {
        this.proveedores = proveedores;
    }
}
