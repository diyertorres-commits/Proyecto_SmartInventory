package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.CustomerService;
import unl.edu.cc.rest.jbrew.business.ValidationService;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import java.io.Serializable;
import java.util.List;


@Named
@ViewScoped
public class ClienteBean implements Serializable {

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private CustomerService customerService;

    private Customer selectedCustomer;
    private List<Customer> filteredCustomers;
    private String searchTerm;
    private boolean initialized = false;

    public ClienteBean() {
        this.selectedCustomer = new Customer();
        this.filteredCustomers = List.of();
        this.searchTerm = "";
    }

    public void prepareNewCustomer() {
        this.selectedCustomer = new Customer();
    }

    public void editCustomer(Customer customer) {
        this.selectedCustomer = customer;
    }

    public String saveCustomer() {
        CustomerService.CustomerResult resultado = customerService.validateAndSave(selectedCustomer);
        
        if (!resultado.isExitoso()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", resultado.getMensaje()));
            return null;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", resultado.getMensaje()));
        
        prepareNewCustomer();
        searchCustomers();
        return null;
    }

    public void deleteCustomer(Customer customer) {
        try {
            customerService.deleteCustomer(customer);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente eliminado correctamente"));
            searchCustomers();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar cliente: " + e.getMessage()));
        }
    }

    public void searchCustomers() {
        filteredCustomers = inventoryFacade.getAllCustomers().stream()
                .filter(c -> searchTerm == null || searchTerm.isEmpty() ||
                        c.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        c.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        c.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
        initialized = true;
    }

    public void clearSearch() {
        searchTerm = "";
        filteredCustomers = inventoryFacade.getAllCustomers();
        initialized = true;
    }

    public void validateIdentification() {
        String cedula = selectedCustomer.getIdentificationNumber();
        if (cedula == null || cedula.isEmpty()) {
            return;
        }
        
        ValidationService.ValidationResult result = customerService.validateIdentification(cedula);
        if (!result.isValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", result.getMessage()));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Válido", "Cédula válida"));
        }
    }

    public void validateEmail() {
        String email = selectedCustomer.getEmail();
        if (email == null || email.isEmpty()) {
            return;
        }
        
        ValidationService.ValidationResult result = customerService.validateEmail(email);
        if (!result.isValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", result.getMessage()));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Válido", "Email válido"));
        }
    }

    // Getters and Setters
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public List<Customer> getFilteredCustomers() {
        if (!initialized) {
            filteredCustomers = inventoryFacade.getAllCustomers();
            initialized = true;
        }
        return filteredCustomers;
    }

    public void setFilteredCustomers(List<Customer> filteredCustomers) {
        this.filteredCustomers = filteredCustomers;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}