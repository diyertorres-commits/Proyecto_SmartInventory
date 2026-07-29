package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

@Stateless
public class SupplierService {

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private ValidationService validationService;

    public SupplierResult validateAndSave(Supplier supplier) {
        // Validar identificación (RUC/Cédula)
        ValidationService.ValidationResult identificacionResult = validationService.validateIdentificacion(supplier.getIdentificationNumber());
        if (!identificacionResult.isValid()) {
            return SupplierResult.error(identificacionResult.getMessage());
        }

        // Validar nombre
        ValidationService.ValidationResult nombreResult = validationService.validateNombre(supplier.getName());
        if (!nombreResult.isValid()) {
            return SupplierResult.error("El nombre comercial es obligatorio.");
        }

        // Validar contacto
        ValidationService.ValidationResult contactoResult = validationService.validateNombre(supplier.getContacto());
        if (!contactoResult.isValid()) {
            return SupplierResult.error("La persona de contacto es obligatoria.");
        }

        // Validar teléfono
        ValidationService.ValidationResult telefonoResult = validationService.validateTelefono(supplier.getPhone());
        if (!telefonoResult.isValid()) {
            return SupplierResult.error(telefonoResult.getMessage());
        }

        // Validar email
        ValidationService.ValidationResult emailResult = validationService.validateEmail(supplier.getEmail());
        if (!emailResult.isValid()) {
            return SupplierResult.error(emailResult.getMessage());
        }

        try {
            inventoryFacade.saveSupplier(supplier);
            String mensaje = supplier.getIdSupplier() == 0 ? "Proveedor creado correctamente" : "Proveedor actualizado correctamente";
            return SupplierResult.success(mensaje);
        } catch (Exception e) {
            return SupplierResult.error("Error al guardar proveedor: " + e.getMessage());
        }
    }

    public void deleteSupplier(Supplier supplier) {
        try {
            inventoryFacade.deleteSupplier(supplier);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar proveedor: " + e.getMessage(), e);
        }
    }

    public ValidationService.ValidationResult validateIdentification(String identification) {
        return validationService.validateIdentificacion(identification);
    }

    public ValidationService.ValidationResult validateEmail(String email) {
        return validationService.validateEmail(email);
    }

    public static class SupplierResult {
        private final boolean exitoso;
        private final String mensaje;

        private SupplierResult(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public static SupplierResult success(String mensaje) {
            return new SupplierResult(true, mensaje);
        }

        public static SupplierResult error(String mensaje) {
            return new SupplierResult(false, mensaje);
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
