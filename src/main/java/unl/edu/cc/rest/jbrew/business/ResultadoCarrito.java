package unl.edu.cc.rest.jbrew.business;

public class ResultadoCarrito {

    private final boolean exitoso;
    private final String mensaje;

    private ResultadoCarrito(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public static ResultadoCarrito exito(String mensaje) {
        return new ResultadoCarrito(true, mensaje);
    }

    public static ResultadoCarrito error(String mensaje) {
        return new ResultadoCarrito(false, mensaje);
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }
}