package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductNameException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductPriceException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductStockException;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;
import unl.edu.cc.rest.jbrew.domain.Movements.MovementType;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class PurchaseService {

    private static final Logger LOGGER = Logger.getLogger(PurchaseService.class.getName());

    @Inject
    private InventoryService inventoryService;

    private final List<PurchaseRecord> purchaseHistory = new ArrayList<>();
    private final List<PurchaseInvoice> purchaseInvoices = new ArrayList<>();
    private int invoiceCounter = 1;

    @Lock(LockType.WRITE)
    public PurchaseResult processRestockPurchase(Product product, int quantity, double purchasePrice, Supplier supplier) {
        if (supplier == null) {
            return new PurchaseResult(false, "Proveedor no seleccionado", null, null);
        }

        Optional<Product> productOpt = inventoryService.findProductById(product.getIdProduct());
        if (productOpt.isEmpty()) {
            return new PurchaseResult(false, "Producto no encontrado", null, null);
        }

        Product existingProduct = productOpt.get();
        try {
            existingProduct.setPurchasePrice(purchasePrice);

            Movement movimiento = new Movement(
                    nextMovementId(),
                    MovementType.ENTRY,
                    new Date(),
                    "Compra - reabastecimiento (" + supplier.getName() + ")"
            );
            movimiento.addProductMovement(existingProduct, quantity, purchasePrice);
            movimiento.processMovement(); // aumenta stock real y registra Kardex

            PurchaseRecord record = createPurchaseRecord("REABASTECER", existingProduct, quantity, purchasePrice, supplier);
            purchaseHistory.add(record);

            PurchaseInvoice invoice = createPurchaseInvoice(supplier);
            invoice.setMovement(movimiento);
            invoice.generateInvoice(); // total = movimiento.calculateTotal()
            purchaseInvoices.add(invoice);

            return new PurchaseResult(true, "Stock reabastecido correctamente. Factura #" + invoice.getInvoiceNumber() + " generada", record, invoice);
        } catch (InvalidProductStockException | InvalidProductPriceException e) {
            return new PurchaseResult(false, "Error de validación: " + e.getMessage(), null, null);
        }
    }

    @Lock(LockType.WRITE)
    public PurchaseResult processNewProductPurchase(Product newProduct, Supplier supplier) {
        if (supplier == null) {
            return new PurchaseResult(false, "Proveedor no seleccionado", null, null);
        }

        if (newProduct.getCodigo() == null || newProduct.getCodigo().isEmpty() ||
                newProduct.getName() == null || newProduct.getName().isEmpty() ||
                newProduct.getCategory() == null) {
            return new PurchaseResult(false, "Complete los campos obligatorios", null, null);
        }

        try {
            inventoryService.saveProduct(newProduct);

            PurchaseRecord record = createPurchaseRecord("ADQUIRIR", newProduct, newProduct.getStock(), newProduct.getPurchasePrice(), supplier);
            purchaseHistory.add(record);

            PurchaseInvoice invoice = createPurchaseInvoice(supplier);
            // Sin Movement aquí: el stock inicial ya se fijó al crear el
            // producto, así que el total se calcula directamente.
            invoice.setTotal(newProduct.getStock() * newProduct.getPurchasePrice());
            purchaseInvoices.add(invoice);

            return new PurchaseResult(true, "Nuevo producto adquirido correctamente. Factura #" + invoice.getInvoiceNumber() + " generada", record, invoice);
        } catch (InvalidProductNameException | InvalidProductPriceException | InvalidProductStockException e) {
            return new PurchaseResult(false, "Error de validación: " + e.getMessage(), null, null);
        }
    }

    private int nextMovementId() {
        // Simplificación: en producción debería venir de una secuencia
        // real de base de datos, igual que se señaló en VentaService.
        return purchaseInvoices.size() + 1;
    }

    private PurchaseRecord createPurchaseRecord(String type, Product product, int quantity, double purchasePrice, Supplier supplier) {
        PurchaseRecord record = new PurchaseRecord();
        record.setId(purchaseHistory.size() + 1);
        record.setFecha(new Date());
        record.setTipo(type);
        record.setProductoNombre(product.getName());
        record.setCantidad(quantity);
        record.setPrecioCompra(purchasePrice);
        record.setTotal(quantity * purchasePrice);
        record.setProveedorNombre(supplier.getName());
        return record;
    }

    private PurchaseInvoice createPurchaseInvoice(Supplier supplier) {
        int numeroFactura = invoiceCounter++;

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setIdInvoice(numeroFactura);
        invoice.setInvoiceDate(new Date());
        invoice.setInvoiceNumber("FAC-COMP-" + String.format("%06d", numeroFactura));
        // FIX: antes usaba "invoiceCounter" ya incrementado, quedando un
        // número por delante de invoiceNumber. Ahora usan el mismo valor.
        invoice.setPurchaseOrderNumber("PO-" + String.format("%06d", numeroFactura));
        invoice.setSupplier(supplier);
        return invoice;
    }

    @Lock(LockType.READ)
    public List<PurchaseRecord> getPurchaseHistory() {
        return new ArrayList<>(purchaseHistory);
    }

    @Lock(LockType.READ)
    public List<PurchaseInvoice> getPurchaseInvoices() {
        return new ArrayList<>(purchaseInvoices);
    }

    @Lock(LockType.READ)
    public double getTotalPurchases() {
        return purchaseHistory.stream()
                .mapToDouble(PurchaseRecord::getTotal)
                .sum();
    }

    // Record classes for data transfer
    public static class PurchaseRecord {
        private int id;
        private Date fecha;
        private String tipo;
        private String productoNombre;
        private int cantidad;
        private double precioCompra;
        private double total;
        private String proveedorNombre;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public Date getFecha() { return fecha; }
        public void setFecha(Date fecha) { this.fecha = fecha; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public double getPrecioCompra() { return precioCompra; }
        public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }

        public String getProveedorNombre() { return proveedorNombre; }
        public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }

        public String getFechaTexto() {
            if (fecha != null) {
                return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
            }
            return "";
        }
    }

    public static class PurchaseResult {
        private final boolean success;
        private final String message;
        private final PurchaseRecord record;
        private final PurchaseInvoice invoice;

        public PurchaseResult(boolean success, String message, PurchaseRecord record, PurchaseInvoice invoice) {
            this.success = success;
            this.message = message;
            this.record = record;
            this.invoice = invoice;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public PurchaseRecord getRecord() { return record; }
        public PurchaseInvoice getInvoice() { return invoice; }
    }
}