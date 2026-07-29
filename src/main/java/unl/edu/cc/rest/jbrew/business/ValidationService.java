package unl.edu.cc.rest.jbrew.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@ApplicationScoped
public class ValidationService {

    private static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public ValidationResult validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return ValidationResult.error("El correo electrónico es obligatorio.");
        }

        email = email.trim();
        if (!email.matches(REGEX_EMAIL)) {
            return ValidationResult.error("El correo electrónico no tiene un formato válido.");
        }

        return ValidationResult.success("Correo electrónico válido.");
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches(REGEX_EMAIL);
    }

    public ValidationResult validateCedulaEcuatoriana(@NotNull @NotEmpty String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return ValidationResult.error("La cédula es obligatoria.");
        }

        if (!cedula.matches("\\d{10}")) {
            return ValidationResult.error("La cédula debe tener exactamente 10 dígitos.");
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return ValidationResult.error("La provincia de la cédula no es válida.");
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito >= 6) {
            return ValidationResult.error("El tercer dígito de la cédula no es válido.");
        }

        if (!validarDigitoVerificador(cedula)) {
            return ValidationResult.error("La cédula ecuatoriana no es válida.");
        }

        return ValidationResult.success("Cédula válida.");
    }

    public static boolean isValidCedulaEcuatoriana(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return false;
        }

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

        return validarDigitoVerificador(cedula);
    }

    private static boolean validarDigitoVerificador(String cedula) {
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));

            if (i % 2 == 0) {
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

    public ValidationResult validateRUC(String ruc) {
        if (ruc == null || ruc.isBlank()) {
            return ValidationResult.error("El RUC es obligatorio.");
        }

        if (!ruc.matches("\\d{13}")) {
            return ValidationResult.error("El RUC debe tener 13 dígitos numéricos.");
        }

        return ValidationResult.success("RUC válido.");
    }

    public static boolean isValidRUC(String ruc) {
        if (ruc == null || ruc.isBlank()) {
            return false;
        }
        return ruc.matches("\\d{13}");
    }

    public ValidationResult validateIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.isBlank()) {
            return ValidationResult.error("La identificación es obligatoria.");
        }

        if (identificacion.length() == 10) {
            return validateCedulaEcuatoriana(identificacion);
        } else if (identificacion.length() == 13) {
            return validateRUC(identificacion);
        } else {
            return ValidationResult.error("La identificación debe tener 10 (cédula) o 13 (RUC) dígitos.");
        }
    }

    public ValidationResult validateTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return ValidationResult.error("El teléfono es obligatorio.");
        }

        if (!telefono.matches("\\d{10}")) {
            return ValidationResult.error("El teléfono debe tener exactamente 10 dígitos numéricos.");
        }

        return ValidationResult.success("Teléfono válido.");
    }

    public static boolean isValidTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return false;
        }
        return telefono.matches("\\d{10}");
    }

    public ValidationResult validateNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return ValidationResult.error("El nombre es obligatorio.");
        }
        return ValidationResult.success("Nombre válido.");
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success(String message) {
            return new ValidationResult(true, message);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
