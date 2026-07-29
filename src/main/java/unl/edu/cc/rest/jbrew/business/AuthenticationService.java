package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import unl.edu.cc.rest.jbrew.exception.CredentialInvalidException;
import unl.edu.cc.rest.jbrew.exception.EntityNotFoundException;
import java.util.logging.Logger;

@Stateless
public class AuthenticationService {
    
    private static final int USUARIO_LONGITUD_MINIMA = 5;
    private static final int PASSWORD_LONGITUD_MINIMA = 6;
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    
    public AuthenticationResult authenticate(String username, String password) {
        LOGGER.info("Intento de autenticación para usuario: " + username);
        
        if (username == null || username.isBlank()) {
            LOGGER.warning("Autenticación fallida: usuario vacío");
            throw new CredentialInvalidException("El usuario es requerido");
        }
        
        if (username.length() < USUARIO_LONGITUD_MINIMA) {
            LOGGER.warning("Autenticación fallida: usuario muy corto");
            throw new CredentialInvalidException("El usuario debe tener al menos " + USUARIO_LONGITUD_MINIMA + " caracteres");
        }
        
        if (password == null || password.isBlank()) {
            LOGGER.warning("Autenticación fallida: contraseña vacía");
            throw new CredentialInvalidException("La contraseña es requerida");
        }
        
        if (password.length() < PASSWORD_LONGITUD_MINIMA) {
            LOGGER.warning("Autenticación fallida: contraseña muy corta");
            throw new CredentialInvalidException("La contraseña debe tener al menos " + PASSWORD_LONGITUD_MINIMA + " caracteres");
        }
        
        if ("admin".equals(username) && "admin123".equals(password)) {
            LOGGER.info("Autenticación exitosa para usuario: " + username);
            return new AuthenticationResult(true, "Bienvenido " + username, username);
        }
        
        LOGGER.warning("Autenticación fallida: credenciales incorrectas para usuario: " + username);
        throw new CredentialInvalidException("Usuario o contraseña incorrectos");
    }
    
    public static class AuthenticationResult {
        private final boolean success;
        private final String message;
        private final String username;
        
        public AuthenticationResult(boolean success, String message, String username) {
            this.success = success;
            this.message = message;
            this.username = username;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUsername() { return username; }
    }
}
