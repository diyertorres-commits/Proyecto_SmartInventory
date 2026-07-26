package unl.edu.cc.rest.jbrew.domain.Sales;

import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras como objeto de dominio real: sabe agregar, eliminar
 * y calcular sus propios totales. No conoce nada de inventario, JSF ni
 * persistencia — esas responsabilidades quedan en la capa de servicio
 * (CarritoService / VentaService), que sí puede consultar otras fuentes
 * de datos.
 *
 * No es un bean CDI ni tiene ámbito propio: cada VentaBean (@ViewScoped)
 * crea el suyo. Así se evita usar @SessionScoped/@ApplicationScoped para
 * mantener el carrito.
 */
public class Carrito implements Serializable {

    private static final double TASA_IVA = 0.12;

    private final List<ItemCarrito> items = new ArrayList<>();

    public List<ItemCarrito> getItems() {
        return items;
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