package unl.edu.cc.rest.jbrew.domain.Sales;

import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;

/**
 * Línea del carrito de compras. Se construye siempre a partir de un
 * Product real (no de un id suelto), y encapsula su propia lógica de
 * incremento de cantidad y cálculo de subtotal.
 */
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