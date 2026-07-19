package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    
    private String username;
    private String password;
    private boolean rememberMe;
    private boolean loggedIn;
    
    public LoginBean() {
    }
    
    public String login() {
        // Validación básica - en producción esto debería ir contra base de datos
        if (username != null && password != null && 
            !username.isEmpty() && !password.isEmpty()) {
            
            // Para demo: usuario específico
            // En producción: validar contra base de datos real
            if (username.equals("admin") && password.equals("admin123")) {
                loggedIn = true;
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Bienvenido " + username));
                return "/products.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos"));
                return null;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Por favor ingrese usuario y contraseña"));
            return null;
        }
    }
    
    public String logout() {
        loggedIn = false;
        username = null;
        password = null;
        rememberMe = false;
        
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Sesión cerrada correctamente"));
        
        return "/login.xhtml?faces-redirect=true";
    }
    
    public boolean isLoggedIn() {
        return loggedIn;
    }
    
    // Getters y Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isRememberMe() {
        return rememberMe;
    }
    
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
    
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
