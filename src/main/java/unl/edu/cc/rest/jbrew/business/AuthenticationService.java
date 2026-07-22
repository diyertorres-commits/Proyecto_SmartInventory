package unl.edu.cc.rest.jbrew.business;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticationService {
    
    public AuthenticationResult authenticate(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return new AuthenticationResult(false, "Por favor ingrese usuario y contraseña", null);
        }
        
        if ("admin".equals(username) && "admin123".equals(password)) {
            return new AuthenticationResult(true, "Bienvenido " + username, username);
        }
        
        return new AuthenticationResult(false, "Usuario o contraseña incorrectos", null);
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
