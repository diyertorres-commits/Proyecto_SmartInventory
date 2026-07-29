package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.AuthenticationService;
import unl.edu.cc.rest.jbrew.business.SecurityFacade;
import unl.edu.cc.rest.jbrew.exception.CredentialInvalidException;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@RequestScoped
public class LoginBean implements Serializable {

    private static final int USUARIO_LONGITUD_MINIMA = 5;
    private static final int PASSWORD_LONGITUD_MINIMA = 6;

    private static final Logger LOGGER = Logger.getLogger(LoginBean.class.getName());

    @Inject
    private SecurityFacade securityFacade;

    @Inject
    private SesionUsuario sesionUsuario;

    @Inject
    private AlertaStockBean alertaStockBean;

    private String username;
    private String password;
    private boolean rememberMe;

    public String login() {
        if (!credencialesValidas()) {
            return null;
        }

        LOGGER.info("Intento de login para usuario: " + username);
        try {
            AuthenticationService.AuthenticationResult resultado = securityFacade.authenticate(username, password);
            sesionUsuario.iniciarSesion(username);
            
            // Verificar alertas de stock bajo al iniciar sesión
            alertaStockBean.verificarStockBajo();
            
            LOGGER.info("Login exitoso para usuario: " + username);
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", resultado.getMessage());
            return "/products.xhtml?faces-redirect=true";
        } catch (CredentialInvalidException e) {
            LOGGER.warning("Login fallido para usuario: " + username);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            return null;
        }
    }

    public String logout() {
        sesionUsuario.cerrarSesion();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    private boolean credencialesValidas() {
        if (username == null || username.isBlank()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "El usuario es requerido");
            return false;
        }
        if (username.length() < USUARIO_LONGITUD_MINIMA) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia",
                    "El usuario debe tener al menos " + USUARIO_LONGITUD_MINIMA + " caracteres");
            return false;
        }
        if (password == null || password.isBlank()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "La contraseña es requerida");
            return false;
        }
        if (password.length() < PASSWORD_LONGITUD_MINIMA) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia",
                    "La contraseña debe tener al menos " + PASSWORD_LONGITUD_MINIMA + " caracteres");
            return false;
        }
        return true;
    }

    private void mostrarMensaje(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, resumen, detalle));
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isRememberMe() { return rememberMe; }
    public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
}