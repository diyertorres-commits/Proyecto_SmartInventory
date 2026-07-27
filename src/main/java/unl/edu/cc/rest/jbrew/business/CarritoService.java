package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Sales.Carrito;
import unl.edu.cc.rest.jbrew.domain.Sales.ItemCarrito;

import java.util.Optional;

@Stateless
public class CarritoService {

    @Inject
    private InventoryService inventoryService;

    public ResultadoCarrito agregarProducto(Carrito carrito, Product producto, int cantidad) {
        if (producto == null) {
            return ResultadoCarrito.error("Producto no válido");
        }
        if (cantidad <= 0) {
            return ResultadoCarrito.error("La cantidad debe ser mayor a cero");
        }

        Optional<Product> productoActual = inventoryService.findProductById(producto.getIdProduct());
        if (productoActual.isEmpty()) {
            return ResultadoCarrito.error("Producto no encontrado");
        }

        Product productoVigente = productoActual.get();
        int reservadoEnCarrito = carrito.cantidadReservadaDe(productoVigente.getIdProduct());

        if (reservadoEnCarrito + cantidad > productoVigente.getStock()) {
            return ResultadoCarrito.error("Stock insuficiente. Disponible: " + productoVigente.getStock());
        }

        // El stock real NO se descuenta aquí, solo se valida contra lo
        // disponible. El descuento real ocurre al confirmar la venta,
        // en VentaService.registrarVenta().
        carrito.agregarItem(productoVigente, cantidad);
        return ResultadoCarrito.exito("Producto agregado al carrito");
    }

    public ResultadoCarrito eliminarProducto(Carrito carrito, ItemCarrito item) {
        return carrito.eliminarItem(item)
                ? ResultadoCarrito.exito("Producto eliminado del carrito")
                : ResultadoCarrito.error("El producto no estaba en el carrito");
    }
}