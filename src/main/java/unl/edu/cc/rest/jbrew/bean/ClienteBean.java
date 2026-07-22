package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ClienteBean implements Serializable {
    
    @Inject
    private InventoryService inventoryService;
    
    private Customer selectedCustomer;
    private List<Customer> filteredCustomers;
    private String searchTerm;
    
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
        try {
            inventoryService.saveCustomer(selectedCustomer);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", 
                    selectedCustomer.getIdCustomer() == 0 ? "Cliente creado correctamente" : "Cliente actualizado correctamente"));
            prepareNewCustomer();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar cliente: " + e.getMessage()));
            return null;
        }
    }
    
    public void deleteCustomer(Customer customer) {
        try {
            inventoryService.deleteCustomer(customer);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente eliminado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar cliente: " + e.getMessage()));
        }
    }
    
    public void searchCustomers() {
        filteredCustomers = inventoryService.getAllCustomers().stream()
                .filter(c -> searchTerm == null || searchTerm.isEmpty() || 
                    c.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    c.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    c.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }
    
    public void clearSearch() {
        searchTerm = "";
        filteredCustomers = inventoryService.getAllCustomers();
    }
    
    // Getters and Setters
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public Customer getClienteSeleccionado() {
        return getSelectedCustomer();
    }
    
    public Customer getCliente() {
        return getSelectedCustomer();
    }
    
    public int getClienteId() {
        return selectedCustomer != null ? selectedCustomer.getIdCustomer() : 0;
    }
    
    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    public void setClienteId(int customerId) {
        Customer customer = inventoryService.findCustomerById(customerId).orElse(null);
        setSelectedCustomer(customer);
    }
    
    public void setClienteSeleccionado(Customer selectedCustomer) {
        setSelectedCustomer(selectedCustomer);
    }
    
    public List<Customer> getFilteredCustomers() {
        if (filteredCustomers.isEmpty()) {
            filteredCustomers = inventoryService.getAllCustomers();
        }
        return filteredCustomers;
    }
    
    public List<Customer> getClientesFiltrados() {
        return getFilteredCustomers();
    }
    
    public List<Customer> getClientes() {
        return getFilteredCustomers();
    }
    
    public void setFilteredCustomers(List<Customer> filteredCustomers) {
        this.filteredCustomers = filteredCustomers;
    }
    
    public void setClientesFiltrados(List<Customer> filteredCustomers) {
        setFilteredCustomers(filteredCustomers);
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public String getTerminoBusqueda() {
        return getSearchTerm();
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public void setTerminoBusqueda(String searchTerm) {
        setSearchTerm(searchTerm);
    }
}
