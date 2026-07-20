package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class ClienteBean implements Serializable {
    
    @Inject
    private InventarioBean inventarioBean;
    
    private Customer cliente;
    private List<Customer> clientes;
    private List<Customer> clientesFiltrados;
    private String searchTerm;
    
    public ClienteBean() {
        this.cliente = new Customer();
        this.clientes = new ArrayList<>();
        this.clientesFiltrados = new ArrayList<>();
        this.searchTerm = "";
    }
    
    public void prepararNuevo() {
        this.cliente = new Customer();
    }
    
    public void editar(Customer cliente) {
        this.cliente = cliente;
    }
    
    public String guardar() {
        try {
            if (cliente.getIdCustomer() == 0) {
                // Nuevo cliente
                cliente.setIdCustomer(inventarioBean.getClientes().size() + 1);
                inventarioBean.getClientes().add(cliente);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente creado correctamente"));
            } else {
                // Editar cliente existente
                // Actualizar en la lista
                for (int i = 0; i < inventarioBean.getClientes().size(); i++) {
                    if (inventarioBean.getClientes().get(i).getIdCustomer() == cliente.getIdCustomer()) {
                        inventarioBean.getClientes().set(i, cliente);
                        break;
                    }
                }
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente actualizado correctamente"));
            }
            prepararNuevo();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar cliente: " + e.getMessage()));
            return null;
        }
    }
    
    public void eliminar(Customer cliente) {
        try {
            inventarioBean.getClientes().remove(cliente);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente eliminado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar cliente: " + e.getMessage()));
        }
    }
    
    // Getters y Setters
    public Customer getCliente() {
        return cliente;
    }
    
    public void setCliente(Customer cliente) {
        this.cliente = cliente;
    }
    
    public List<Customer> getClientes() {
        if (clientes.isEmpty()) {
            clientes = inventarioBean.getClientes();
            clientesFiltrados = new ArrayList<>(clientes);
        }
        return clientes;
    }
    
    public List<Customer> getClientesFiltrados() {
        if (clientesFiltrados.isEmpty()) {
            clientesFiltrados = new ArrayList<>(inventarioBean.getClientes());
        }
        return clientesFiltrados;
    }
    
    public void setClientesFiltrados(List<Customer> clientesFiltrados) {
        this.clientesFiltrados = clientesFiltrados;
    }
    
    public void search() {
        clientesFiltrados = new ArrayList<>();
        for (Customer c : inventarioBean.getClientes()) {
            if (searchTerm == null || searchTerm.isEmpty() || 
                c.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                c.getIdentificationNumber().toLowerCase().contains(searchTerm.toLowerCase()) ||
                c.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) {
                clientesFiltrados.add(c);
            }
        }
    }
    
    public void clearSearch() {
        searchTerm = "";
        clientesFiltrados = new ArrayList<>(inventarioBean.getClientes());
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public void setClientes(List<Customer> clientes) {
        this.clientes = clientes;
    }
}
