package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.SupplierService;
import unl.edu.cc.rest.jbrew.business.ValidationService;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProveedorBean implements Serializable {

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private SupplierService supplierService;

    private Supplier selectedSupplier;
    private List<Supplier> filteredSuppliers;
    private String searchTerm;
    private boolean initialized = false;

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

    public String saveSupplier() {
        SupplierService.SupplierResult resultado = supplierService.validateAndSave(selectedSupplier);
        
        if (!resultado.isExitoso()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", resultado.getMensaje()));
            return null;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", resultado.getMensaje()));
        
        prepareNewSupplier();
        searchSuppliers();
        return null;
    }

    public void deleteSupplier(Supplier supplier) {
        try {
            supplierService.deleteSupplier(supplier);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Proveedor eliminado correctamente"));
            searchSuppliers();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar proveedor: " + e.getMessage()));
        }
    }

    public void searchSuppliers() {
        filteredSuppliers = inventoryFacade.getAllSuppliers().stream()
                .filter(s -> searchTerm == null || searchTerm.isEmpty() ||
                        s.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        s.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        s.getContacto().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
        initialized = true;
    }

    public void clearSearch() {
        searchTerm = "";
        filteredSuppliers = inventoryFacade.getAllSuppliers();
        initialized = true;
    }

    public void validateIdentification() {
        String identification = selectedSupplier.getIdentificationNumber();
        if (identification == null || identification.isEmpty()) {
            return;
        }
        
        ValidationService.ValidationResult result = supplierService.validateIdentification(identification);
        if (!result.isValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", result.getMessage()));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Válido", "Identificación válida"));
        }
    }

    public void validateEmail() {
        String email = selectedSupplier.getEmail();
        if (email == null || email.isEmpty()) {
            return;
        }
        
        ValidationService.ValidationResult result = supplierService.validateEmail(email);
        if (!result.isValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", result.getMessage()));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Válido", "Email válido"));
        }
    }

    // Getters and Setters
    public Supplier getSelectedSupplier() {
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    public List<Supplier> getFilteredSuppliers() {
        if (!initialized) {
            filteredSuppliers = inventoryFacade.getAllSuppliers();
            initialized = true;
        }
        return filteredSuppliers;
    }

    public void setFilteredSuppliers(List<Supplier> filteredSuppliers) {
        this.filteredSuppliers = filteredSuppliers;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}