package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProveedorBean implements Serializable {
    
    @Inject
    private InventoryFacade inventoryFacade;
    
    private Supplier selectedSupplier;
    private List<Supplier> filteredSuppliers;
    private String searchTerm;
    
    public ProveedorBean() {
        this.selectedSupplier = new Supplier();
        this.filteredSuppliers = List.of();
        this.searchTerm = "";
    }
    
    public void prepareNewSupplier() {
        this.selectedSupplier = new Supplier();
    }
    
    public void editSupplier(Supplier supplier) {
        this.selectedSupplier = supplier;
    }
    
    public void editar(Supplier supplier) {
        editSupplier(supplier);
    }
    
    public String saveSupplier() {
        try {
            inventoryFacade.saveSupplier(selectedSupplier);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    selectedSupplier.getIdSupplier() == 0 ? "Proveedor creado correctamente" : "Proveedor actualizado correctamente"));
            prepareNewSupplier();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar proveedor: " + e.getMessage()));
            return null;
        }
    }
    
    public String guardar() {
        return saveSupplier();
    }
    
    public void deleteSupplier(Supplier supplier) {
        try {
            inventoryFacade.deleteSupplier(supplier);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Proveedor eliminado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar proveedor: " + e.getMessage()));
        }
    }
    
    public void eliminar(Supplier supplier) {
        deleteSupplier(supplier);
    }
    
    public void searchSuppliers() {
        filteredSuppliers = inventoryFacade.getAllSuppliers().stream()
                .filter(s -> searchTerm == null || searchTerm.isEmpty() || 
                    s.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    s.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    s.getContacto().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }
    
    public void search() {
        searchSuppliers();
    }
    
    public void clearSearch() {
        searchTerm = "";
        filteredSuppliers = inventoryFacade.getAllSuppliers();
    }
    
    // Getters and Setters
    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }
    
    public Supplier getProveedor() {
        return getSelectedSupplier();
    }
    
    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }
    
    public void setProveedor(Supplier selectedSupplier) {
        setSelectedSupplier(selectedSupplier);
    }
    
    public List<Supplier> getFilteredSuppliers() {
        if (filteredSuppliers.isEmpty()) {
            filteredSuppliers = inventoryFacade.getAllSuppliers();
        }
        return filteredSuppliers;
    }
    
    public List<Supplier> getProveedoresFiltrados() {
        return getFilteredSuppliers();
    }
    
    public List<Supplier> getProveedores() {
        return getFilteredSuppliers();
    }
    
    public void setFilteredSuppliers(List<Supplier> filteredSuppliers) {
        this.filteredSuppliers = filteredSuppliers;
    }
    
    public void setProveedoresFiltrados(List<Supplier> filteredSuppliers) {
        setFilteredSuppliers(filteredSuppliers);
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
