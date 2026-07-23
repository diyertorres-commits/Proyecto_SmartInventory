package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import unl.edu.cc.rest.jbrew.business.AuthenticationService;
import unl.edu.cc.rest.jbrew.business.SecurityFacade;
import unl.edu.cc.rest.jbrew.exception.CredentialInvalidException;
import java.io.Serializable;
import java.util.logging.Logger;

@Named
@RequestScoped
public class LoginBean implements Serializable {
    
    private static final Logger LOGGER = Logger.getLogger(LoginBean.class.getName());
    
    @Inject
    private SecurityFacade securityFacade;
    
    @NotNull(message = "El usuario es requerido")
    @NotEmpty(message = "El usuario no puede estar vacío")
    @Size(min = 5, message = "El usuario debe tener al menos 5 caracteres")
    private String username;
    
    @NotNull(message = "La contraseña es requerida")
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private boolean rememberMe;
    
    public LoginBean() {
    }
    
    public String login() {
        LOGGER.info("Intento de login para usuario: " + username);
        try {
            AuthenticationService.AuthenticationResult result = securityFacade.authenticate(username, password);
            LOGGER.info("Login exitoso para usuario: " + username);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            return "/products.xhtml?faces-redirect=true";
        } catch (CredentialInvalidException e) {
            LOGGER.warning("Login fallido para usuario: " + username);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }
    
    public String logout() {
        username = null;
        password = null;
        rememberMe = false;
        
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Sesión cerrada correctamente"));
        
        return "/login.xhtml?faces-redirect=true";
    }
    
    // Getters and Setters
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
}
