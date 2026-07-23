package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ClienteBean implements Serializable {
    
    @Inject
    private InventoryFacade inventoryFacade;
    
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
    
    public void editar(Customer customer) {
        editCustomer(customer);
    }
    
    public String saveCustomer() {
        try {
            inventoryFacade.saveCustomer(selectedCustomer);
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
    
    public String guardar() {
        return saveCustomer();
    }
    
    public void deleteCustomer(Customer customer) {
        try {
            inventoryFacade.deleteCustomer(customer);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente eliminado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar cliente: " + e.getMessage()));
        }
    }
    
    public void eliminar(Customer customer) {
        deleteCustomer(customer);
    }
    
    public void searchCustomers() {
        filteredCustomers = inventoryFacade.getAllCustomers().stream()
                .filter(c -> searchTerm == null || searchTerm.isEmpty() || 
                    c.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    c.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    c.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }
    
    public void search() {
        searchCustomers();
    }
    
    public void clearSearch() {
        searchTerm = "";
        filteredCustomers = inventoryFacade.getAllCustomers();
    }
    
    // Getters and Setters
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public Customer getCliente() {
        return getSelectedCustomer();
    }
    
    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    public void setCliente(Customer selectedCustomer) {
        setSelectedCustomer(selectedCustomer);
    }
    
    public List<Customer> getFilteredCustomers() {
        if (filteredCustomers.isEmpty()) {
            filteredCustomers = inventoryFacade.getAllCustomers();
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
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
