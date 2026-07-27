package unl.edu.cc.rest.jbrew.business;

import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;

public class ResultadoVenta {

    private final boolean exitoso;
    private final String mensaje;
    private final SaleInvoice factura;

    private ResultadoVenta(boolean exitoso, String mensaje, SaleInvoice factura) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.factura = factura;
    }

    public static ResultadoVenta exito(String mensaje, SaleInvoice factura) {
        return new ResultadoVenta(true, mensaje, factura);
    }

    public static ResultadoVenta error(String mensaje) {
        return new ResultadoVenta(false, mensaje, null);
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public SaleInvoice getFactura() {
        return factura;
    }
}