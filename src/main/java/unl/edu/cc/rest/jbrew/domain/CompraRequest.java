package unl.edu.cc.rest.jbrew.domain;

import java.io.Serializable;

public class CompraRequest implements Serializable {

    private int cantidad;
    private double precioCompra;

    public CompraRequest() {
        this.cantidad = 1;
        this.precioCompra = 0;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }
}
