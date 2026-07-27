package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class SesionUsuario implements Serializable {

    private String username;
    private boolean autenticado;

    public void iniciarSesion(String username) {
        this.username = username;
        this.autenticado = true;
    }

    public void cerrarSesion() {
        this.username = null;
        this.autenticado = false;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public String getUsername() {
        return username;
    }
}