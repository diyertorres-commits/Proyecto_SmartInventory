package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.People.Customer;

@Stateless
public class CustomerService {

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private ValidationService validationService;

    public CustomerResult validateAndSave(Customer customer) {
        // Validar cédula
        ValidationService.ValidationResult cedulaResult = validationService.validateCedulaEcuatoriana(customer.getIdentificationNumber());
        if (!cedulaResult.isValid()) {
            return CustomerResult.error(cedulaResult.getMessage());
        }

        // Validar email
        ValidationService.ValidationResult emailResult = validationService.validateEmail(customer.getEmail());
        if (!emailResult.isValid()) {
            return CustomerResult.error(emailResult.getMessage());
        }

        // Validar teléfono
        ValidationService.ValidationResult telefonoResult = validationService.validateTelefono(customer.getPhone());
        if (!telefonoResult.isValid()) {
            return CustomerResult.error(telefonoResult.getMessage());
        }

        // Validar nombre
        ValidationService.ValidationResult nombreResult = validationService.validateNombre(customer.getName());
        if (!nombreResult.isValid()) {
            return CustomerResult.error(nombreResult.getMessage());
        }

        // Validar apellido
        ValidationService.ValidationResult apellidoResult = validationService.validateNombre(customer.getApellido());
        if (!apellidoResult.isValid()) {
            return CustomerResult.error("El apellido es obligatorio.");
        }

        try {
            inventoryFacade.saveCustomer(customer);
            String mensaje = customer.getIdCustomer() == 0 ? "Cliente creado correctamente" : "Cliente actualizado correctamente";
            return CustomerResult.success(mensaje);
        } catch (Exception e) {
            return CustomerResult.error("Error al guardar cliente: " + e.getMessage());
        }
    }

    public void deleteCustomer(Customer customer) {
        try {
            inventoryFacade.deleteCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }

    public ValidationService.ValidationResult validateIdentification(String identification) {
        return validationService.validateCedulaEcuatoriana(identification);
    }

    public ValidationService.ValidationResult validateEmail(String email) {
        return validationService.validateEmail(email);
    }

    public static class CustomerResult {
        private final boolean exitoso;
        private final String mensaje;

        private CustomerResult(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public static CustomerResult success(String mensaje) {
            return new CustomerResult(true, mensaje);
        }

        public static CustomerResult error(String mensaje) {
            return new CustomerResult(false, mensaje);
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
