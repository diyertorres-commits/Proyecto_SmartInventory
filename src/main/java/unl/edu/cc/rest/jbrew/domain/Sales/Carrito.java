package unl.edu.cc.rest.jbrew.domain.Sales;

import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Carrito implements Serializable {

    private static final double TASA_IVA = 0.12;

    private List<ItemCarrito> items = new ArrayList<>();

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public int cantidadReservadaDe(int idProducto) {
        return items.stream()
                .filter(item -> item.getProductoId() == idProducto)
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    public void agregarItem(Product producto, int cantidad) {
        items.stream()
                .filter(item -> item.getProductoId() == producto.getIdProduct())
                .findFirst()
                .ifPresentOrElse(
                        item -> item.incrementarCantidad(cantidad),
                        () -> items.add(new ItemCarrito(producto, cantidad))
                );
    }

    public boolean eliminarItem(ItemCarrito item) {
        return items.remove(item);
    }

    public void vaciar() {
        items.clear();
    }

    public double calcularSubtotal() {
        return items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    public double calcularIva() {
        return calcularSubtotal() * TASA_IVA;
    }

    public double calcularTotal(double descuento) {
        return Math.max(0, calcularSubtotal() + calcularIva() - descuento);
    }
}