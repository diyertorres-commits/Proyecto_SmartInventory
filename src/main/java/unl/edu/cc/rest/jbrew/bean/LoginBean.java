package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.AuthenticationService;
import java.io.Serializable;

@Named
@RequestScoped
public class LoginBean implements Serializable {
    
    @Inject
    private AuthenticationService authenticationService;
    
    private String username;
    private String password;
    private boolean rememberMe;
    
    public LoginBean() {
    }
    
    public String login() {
        AuthenticationService.AuthenticationResult result = authenticationService.authenticate(username, password);
        
        if (result.isSuccess()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", result.getMessage()));
            return "/products.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", result.getMessage()));
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
