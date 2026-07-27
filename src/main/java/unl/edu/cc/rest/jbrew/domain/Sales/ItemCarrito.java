package unl.edu.cc.rest.jbrew.domain.Sales;

import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;

public class ItemCarrito implements Serializable {

    private final int productoId;
    private final String productoNombre;
    private final double precio;
    private int cantidad;

    public ItemCarrito(Product producto, int cantidad) {
        this.productoId = producto.getIdProduct();
        this.productoNombre = producto.getName();
        this.precio = producto.getSalePrice();
        this.cantidad = cantidad;
    }

    public void incrementarCantidad(int cantidadAdicional) {
        this.cantidad += cantidadAdicional;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return precio * cantidad;
    }
}