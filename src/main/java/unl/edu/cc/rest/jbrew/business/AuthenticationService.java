package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import unl.edu.cc.rest.jbrew.exception.CredentialInvalidException;
import unl.edu.cc.rest.jbrew.exception.EntityNotFoundException;
import java.util.logging.Logger;

@Stateless
public class AuthenticationService {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    
    public AuthenticationResult authenticate(String username, String password) {
        LOGGER.info("Intento de autenticación para usuario: " + username);
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            LOGGER.warning("Autenticación fallida: credenciales vacías");
            throw new CredentialInvalidException("Por favor ingrese usuario y contraseña");
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
