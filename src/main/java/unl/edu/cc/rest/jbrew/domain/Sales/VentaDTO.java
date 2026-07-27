package unl.edu.cc.rest.jbrew.domain.Sales;

import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;

import java.io.Serializable;

public class VentaDTO implements Serializable {

    private Product productoSeleccionado;
    private int cantidadSeleccionada;
    private Customer clienteSeleccionado;
    private String metodoPago;
    private double descuento;

    public VentaDTO() {
        this.cantidadSeleccionada = 1;
        this.metodoPago = "efectivo";
        this.descuento = 0;
    }

    public Product getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Product productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public int getCantidadSeleccionada() {
        return cantidadSeleccionada;
    }

    public void setCantidadSeleccionada(int cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }

    public Customer getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Customer clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public void limpiarSeleccionProducto() {
        this.productoSeleccionado = null;
        this.cantidadSeleccionada = 1;
    }

    public void limpiarDatosVenta() {
        this.clienteSeleccionado = null;
        this.descuento = 0;
    }
}
