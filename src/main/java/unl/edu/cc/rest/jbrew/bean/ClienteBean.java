package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    public void editar(Customer customer) {
        editCustomer(customer);
    }

    public String saveCustomer() {
        try {
            // Validar cédula antes de guardar
            String cedula = selectedCustomer.getIdentificationNumber();
            if (cedula == null || cedula.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cédula es obligatoria."));
                return null;
            }

            if (!validarCedula(cedula)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cédula ecuatoriana no es válida."));
                return null;
            }

            // Validar email
            String email = selectedCustomer.getEmail();
            if (email == null || email.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El correo electrónico es obligatorio."));
                return null;
            }

            if (!email.matches(REGEX_EMAIL)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El correo electrónico no tiene un formato válido."));
                return null;
            }

            // Validar teléfono (solo números y 10 dígitos)
            String telefono = selectedCustomer.getPhone();
            if (telefono == null || telefono.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El teléfono es obligatorio."));
                return null;
            }

            if (!telefono.matches("\\d{10}")) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El teléfono debe tener exactamente 10 dígitos numéricos."));
                return null;
            }

            // Validar nombre
            String nombre = selectedCustomer.getName();
            if (nombre == null || nombre.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre es obligatorio."));
                return null;
            }

            // Validar apellido
            String apellido = selectedCustomer.getApellido();
            if (apellido == null || apellido.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El apellido es obligatorio."));
                return null;
            }

            inventoryFacade.saveCustomer(selectedCustomer);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                            selectedCustomer.getIdCustomer() == 0 ? "Cliente creado correctamente" : "Cliente actualizado correctamente"));
            prepareNewCustomer();
            searchCustomers();
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
            searchCustomers();
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
        initialized = true;
    }

    public void search() {
        searchCustomers();
    }

    public void clearSearch() {
        searchTerm = "";
        filteredCustomers = inventoryFacade.getAllCustomers();
        initialized = true;
    }


    private static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public void validatorEmail() {
        String email = selectedCustomer.getEmail();

        if (email == null || email.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "El correo electrónico es obligatorio."
                    )
            );
            return;
        }

        email = email.trim();
        selectedCustomer.setEmail(email);

        if (!email.matches(REGEX_EMAIL)) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "El correo electrónico no tiene un formato válido."
                    )
            );
            return;
        }

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Correcto",
                        "Correo electrónico válido."
                )
        );
    }
    public static boolean validarCedula(@NotNull @NotEmpty String cedula) {

        if (cedula == null || cedula.isBlank()) {
            return false;
        }

        // Debe tener exactamente 10 dígitos
        if (!cedula.matches("\\d{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));

        if (provincia < 1 || provincia > 24) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));

        if (tercerDigito >= 6) {
            return false;
        }

        int suma = 0;

        for (int i = 0; i < 9; i++) {

            int digito = Character.getNumericValue(cedula.charAt(i));

            if (i % 2 == 0) { // índices pares (0,2,4,6,8) = posiciones impares (1,3,5,7,9)
                digito *= 2;

                if (digito > 9) {
                    digito -= 9;
                }
            }

            suma += digito;
        }

        int verificador = 10 - (suma % 10);
        if (verificador == 10) {
            verificador = 0;
        }

        return verificador == Character.getNumericValue(cedula.charAt(9));
    }
    public void validatorCedula() {

        String cedula = selectedCustomer.getIdentificationNumber();

        if (cedula == null || cedula.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "La cédula es obligatoria."
                    )
            );
            return;
        }

        if (!validarCedula(cedula)) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "La cédula ecuatoriana no es válida."
                    )
            );
            return;
        }

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Correcto",
                        "Cédula válida."
                )
        );
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
        if (!initialized) {
            filteredCustomers = inventoryFacade.getAllCustomers();
            initialized = true;
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