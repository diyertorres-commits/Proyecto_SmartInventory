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

    public void editar(Supplier supplier) {
        editSupplier(supplier);
    }

    public String saveSupplier() {
        try {
            // Validar RUC/Cédula
            String identificacion = selectedSupplier.getIdentificationNumber();
            if (identificacion == null || identificacion.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El RUC/Cédula es obligatorio."));
                return null;
            }

            // Validar según longitud (RUC=13, Cédula=10)
            if (identificacion.length() == 10) {
                // Validar como cédula ecuatoriana
                if (!validarCedulaEcuatoriana(identificacion)) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cédula ecuatoriana no es válida."));
                    return null;
                }
            } else if (identificacion.length() == 13) {
                // Validar RUC básico (debe tener 13 dígitos)
                if (!identificacion.matches("\\d{13}")) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El RUC debe tener 13 dígitos numéricos."));
                    return null;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La identificación debe tener 10 (cédula) o 13 (RUC) dígitos."));
                return null;
            }

            // Validar nombre
            String nombre = selectedSupplier.getName();
            if (nombre == null || nombre.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre comercial es obligatorio."));
                return null;
            }

            // Validar contacto
            String contacto = selectedSupplier.getContacto();
            if (contacto == null || contacto.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La persona de contacto es obligatoria."));
                return null;
            }

            // Validar teléfono (solo números y 10 dígitos)
            String telefono = selectedSupplier.getPhone();
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

            // Validar email
            String email = selectedSupplier.getEmail();
            if (email == null || email.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El correo electrónico es obligatorio."));
                return null;
            }

            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            if (!email.matches(emailRegex)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El correo electrónico no tiene un formato válido."));
                return null;
            }

            inventoryFacade.saveSupplier(selectedSupplier);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                            selectedSupplier.getIdSupplier() == 0 ? "Proveedor creado correctamente" : "Proveedor actualizado correctamente"));
            prepareNewSupplier();
            searchSuppliers();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar proveedor: " + e.getMessage()));
            return null;
        }
    }

    public static boolean validarCedulaEcuatoriana(String cedula) {
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

            if (i % 2 == 0) { // posiciones 1,3,5,7,9
                digito *= 2;

                if (digito > 9) {
                    digito -= 9;
                }
            }

            suma += digito;
        }

        int verificador = (10 - (suma % 10)) % 10;

        return verificador == Character.getNumericValue(cedula.charAt(9));
    }

    public void validatorIdentificacion() {
        String identificacion = selectedSupplier.getIdentificationNumber();

        if (identificacion == null || identificacion.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "La identificación es obligatoria."
                    )
            );
            return;
        }

        if (identificacion.length() == 10) {
            if (validarCedulaEcuatoriana(identificacion)) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Correcto",
                                "Cédula válida."
                        )
                );
            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "La cédula ecuatoriana no es válida."
                        )
                );
            }
        } else if (identificacion.length() == 13) {
            if (identificacion.matches("\\d{13}")) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Correcto",
                                "RUC válido."
                        )
                );
            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "El RUC debe tener 13 dígitos numéricos."
                        )
                );
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "La identificación debe tener 10 (cédula) o 13 (RUC) dígitos."
                    )
            );
        }
    }

    public void validatorEmail(String email) {
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

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "El correo electrónico no tiene un formato válido."
                    )
            );
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
            searchSuppliers();
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
        initialized = true;
    }

    public void search() {
        searchSuppliers();
    }

    public void clearSearch() {
        searchTerm = "";
        filteredSuppliers = inventoryFacade.getAllSuppliers();
        initialized = true;
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
        if (!initialized) {
            filteredSuppliers = inventoryFacade.getAllSuppliers();
            initialized = true;
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