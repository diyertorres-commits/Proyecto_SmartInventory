package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;
import unl.edu.cc.rest.jbrew.domain.Movements.MovementType;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.Sales.Carrito;
import unl.edu.cc.rest.jbrew.domain.Sales.ItemCarrito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Singleton
public class VentaService {

    private static final double TASA_IVA = 0.12;

    @Inject
    private InventoryService inventoryService;

    private final List<SaleInvoice> facturas = new ArrayList<>();
    private int contadorFacturas = 1;

    @Lock(LockType.WRITE)
    public ResultadoVenta registrarVenta(Carrito carrito, Customer cliente, String metodoPago, double descuento) {
        if (carrito == null || carrito.estaVacio()) {
            return ResultadoVenta.error("El carrito está vacío");
        }

        Optional<String> errorDeStock = validarStockDisponible(carrito);
        if (errorDeStock.isPresent()) {
            return ResultadoVenta.error(errorDeStock.get());
        }

        Movement movimiento = construirMovimientoDeSalida(carrito, metodoPago);
        movimiento.processMovement();

        SaleInvoice factura = construirFactura(movimiento, cliente, metodoPago, descuento);
        facturas.add(factura);
        carrito.vaciar();

        return ResultadoVenta.exito("Venta completada. Factura #" + factura.getInvoiceNumber() + " generada", factura);
    }

    @Lock(LockType.READ)
    public List<SaleInvoice> obtenerFacturas() {
        return obtenerFacturas("recientes");
    }

    @Lock(LockType.READ)
    public List<SaleInvoice> obtenerFacturas(String orden) {
        List<SaleInvoice> resultado = new ArrayList<>(facturas);
        if ("antiguos".equals(orden)) {
            resultado.sort(Comparator.comparing(SaleInvoice::getInvoiceDate));
        } else {
            resultado.sort(Comparator.comparing(SaleInvoice::getInvoiceDate).reversed());
        }
        return resultado;
    }

    private Optional<String> validarStockDisponible(Carrito carrito) {
        for (ItemCarrito item : carrito.getItems()) {
            Optional<Product> productoActual = inventoryService.findProductById(item.getProductoId());
            if (productoActual.isEmpty()) {
                return Optional.of("El producto '" + item.getProductoNombre() + "' ya no existe");
            }
            if (item.getCantidad() > productoActual.get().getStock()) {
                return Optional.of("Stock insuficiente para '" + item.getProductoNombre() + "'");
            }
        }
        return Optional.empty();
    }

    private Movement construirMovimientoDeSalida(Carrito carrito, String metodoPago) {
        Movement movimiento = new Movement(
                facturas.size() + 1, // TODO: reemplazar por secuencia real de base de datos
                MovementType.EXIT,
                new Date(),
                "Venta - " + metodoPago
        );

        for (ItemCarrito item : carrito.getItems()) {
            Product producto = inventoryService.findProductById(item.getProductoId()).orElseThrow();
            movimiento.addProductMovement(producto, item.getCantidad(), item.getPrecio());
        }
        return movimiento;
    }

    private SaleInvoice construirFactura(Movement movimiento, Customer cliente, String metodoPago, double descuento) {
        SaleInvoice factura = new SaleInvoice();
        factura.setIdInvoice(contadorFacturas++);
        factura.setInvoiceDate(new Date());
        factura.setInvoiceNumber("FAC-" + String.format("%06d", factura.getIdInvoice()));
        factura.setPaymentMethod(metodoPago);
        factura.setCustomer(cliente);
        factura.setMovement(movimiento);

        factura.generateInvoice(); // total = subtotal puro calculado por Movement
        double subtotal = factura.getTotal();
        double iva = subtotal * TASA_IVA;
        double total = Math.max(0, subtotal + iva - descuento);

        factura.setSubtotal(subtotal);
        factura.setTax(iva);
        factura.setDiscount(descuento);
        factura.setTotal(total);
        return factura;
    }
}