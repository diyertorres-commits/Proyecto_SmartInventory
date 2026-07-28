package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.business.service.CrudGenericService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;
import unl.edu.cc.rest.jbrew.domain.Movements.MovementType;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.Sales.Carrito;
import unl.edu.cc.rest.jbrew.domain.Sales.ItemCarrito;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class VentaService {

    private static final double TASA_IVA = 0.12;

    @Inject
    private InventoryService inventoryService;

    @Inject
    private CrudGenericService crudGenericService;

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

        // Guardar movement en base de datos
        crudGenericService.create(movimiento);

        // Actualizar productos en base de datos
        for (ItemCarrito item : carrito.getItems()) {
            Optional<Product> productoOpt = inventoryService.findProductById(item.getProductoId());
            if (productoOpt.isPresent()) {
                inventoryService.saveProduct(productoOpt.get());
            }
        }

        SaleInvoice factura = construirFactura(movimiento, cliente, metodoPago, descuento);
        
        // Guardar factura en base de datos
        crudGenericService.create(factura);
        
        carrito.vaciar();

        return ResultadoVenta.exito("Venta completada. Factura #" + factura.getInvoiceNumber() + " generada", factura);
    }

    public List<SaleInvoice> obtenerFacturas() {
        return obtenerFacturas("recientes");
    }

    public List<SaleInvoice> obtenerFacturas(String orden) {
        List<SaleInvoice> resultado = crudGenericService.findWithQuery("SELECT i FROM SaleInvoice i");
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
                getNextMovementId(),
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

    private int getNextMovementId() {
        // Obtener el último ID de movement de la base de datos
        List<Movement> movements = crudGenericService.findWithQuery("SELECT m FROM Movement m ORDER BY m.id DESC");
        if (movements.isEmpty()) {
            return 1;
        }
        return movements.get(0).getIdMovement() + 1;
    }

    private int getNextInvoiceId() {
        // Obtener el último ID de factura de venta de la base de datos
        List<SaleInvoice> invoices = crudGenericService.findWithQuery("SELECT i FROM SaleInvoice i ORDER BY i.id DESC");
        if (invoices.isEmpty()) {
            return 1;
        }
        return invoices.get(0).getIdInvoice() + 1;
    }

    private SaleInvoice construirFactura(Movement movimiento, Customer cliente, String metodoPago, double descuento) {
        SaleInvoice factura = new SaleInvoice();
        factura.setIdInvoice(getNextInvoiceId());
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